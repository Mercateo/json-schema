package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType
import com.mercateo.jsonschema.generictype.GenericTypeHierarchy
import java.util.concurrent.ConcurrentHashMap

class PropertyBuilderDefault(private val rawPropertyCollectors: List<RawPropertyCollector>) : PropertyBuilder {

    private val genericTypeHierarchy: GenericTypeHierarchy

    private val annotationMapBuilder: AnnotationMapBuilder

    private val knownDescriptors: ConcurrentHashMap<GenericType<*>, PropertyDescriptor>

    init {
        this.genericTypeHierarchy = GenericTypeHierarchy()
        this.annotationMapBuilder = AnnotationMapBuilder()
        this.knownDescriptors = ConcurrentHashMap<GenericType<*>, PropertyDescriptor>()
    }

    override fun from(propertyClass: Class<*>): Property {
        return from(GenericType.of(propertyClass))
    }

    override fun from(genericType: GenericType<*>): Property {
        return from(ROOT_NAME, genericType, mutableMapOf(),
                { it: Any -> rootValueAccessor(it) }, mutableSetOf())
    }

    private fun from(
            name: String,
            genericType: GenericType<*>,
            annotations: Map<Class<out Annotation>, Set<Annotation>>,
            valueAccessor: (Any) -> Any,
            nestedTypes: Set<GenericType<*>>): Property {
        val addedDescriptors = mutableMapOf<GenericType<*>, PropertyDescriptor>()
        val addedReferences = mutableMapOf<GenericType<*>, MutableList<PropertyDescriptorReference>>()
        val property = from(name, genericType, annotations, valueAccessor, addedDescriptors, addedReferences, nestedTypes)
        knownDescriptors.putAll(addedDescriptors)

        for ((key, value) in addedReferences) {
            val propertyDescriptor = knownDescriptors[key]
            value.forEach { propertyDescriptorReference ->
                propertyDescriptorReference.children = propertyDescriptor!!.children
                propertyDescriptorReference.reference = "todo"
            }
        }
        return property
    }

    private fun from(
            name: String,
            genericType: GenericType<*>,
            annotations: Map<Class<out Annotation>, Set<Annotation>>,
            valueAccessor: (Any) -> Any?,
            addedDescriptors: MutableMap<GenericType<*>, PropertyDescriptor>,
            addedReferences: MutableMap<GenericType<*>, MutableList<PropertyDescriptorReference>>,
            nestedTypes: Set<GenericType<*>>): Property {
        val propertyDescriptor = getPropertyDescriptor(genericType, addedDescriptors, addedReferences, nestedTypes)

        return Property(name, propertyDescriptor, valueAccessor, annotationMapBuilder.merge(annotations, propertyDescriptor.annotations))
    }

    private fun getPropertyDescriptor(
            genericType: GenericType<*>,
            addedDescriptors: MutableMap<GenericType<*>, PropertyDescriptor>,
            addedReferences: MutableMap<GenericType<*>, MutableList<PropertyDescriptorReference>>,
            nestedTypes: Set<GenericType<*>>): PropertyDescriptor {
        if (knownDescriptors.containsKey(genericType)) {
            return knownDescriptors[genericType]!!
        } else {
            if (addedDescriptors.containsKey(genericType)) {
                return addedDescriptors[genericType]!!
            } else {
                if (!nestedTypes.contains(genericType)) {
                    return createPropertyDescriptor(genericType, addedDescriptors, addedReferences, nestedTypes)
                } else {
                    val propertyType = PropertyTypeMapper.of(genericType)
                    val annotations = genericType.rawType.annotations
                    val propertyDescriptorReference = PropertyDescriptorReference(propertyType, genericType,
                            annotationMapBuilder.createMap(*annotations))
                    addedReferences.getOrPut(genericType, { mutableListOf<PropertyDescriptorReference>() })
                            .add(propertyDescriptorReference)
                    return propertyDescriptorReference
                }
            }
        }
    }

    private fun createPropertyDescriptor(
            genericType: GenericType<*>,
            addedDescriptors: MutableMap<GenericType<*>, PropertyDescriptor>,
            addedReferences: MutableMap<GenericType<*>, MutableList<PropertyDescriptorReference>>,
            nestedTypes: Set<GenericType<*>>
    ): PropertyDescriptor {
        val propertyType = PropertyTypeMapper.of(genericType)

        val children = when (propertyType) {
            PropertyType.OBJECT -> createChildProperties(genericType, addedDescriptors, addedReferences, nestedTypes + genericType)

            PropertyType.ARRAY ->
                listOf(from("", genericType.containedType, mutableMapOf(), { o -> null }, addedDescriptors, addedReferences, nestedTypes))

            else -> emptyList()
        }

        val annotations = genericType.rawType.annotations.filter { !it.annotationClass.qualifiedName!!.startsWith("kotlin.") }.toTypedArray()
        val propertyDescriptor = PropertyDescriptorDefault(propertyType, genericType, children, annotationMapBuilder.createMap(*annotations))
        addedDescriptors.put(genericType, propertyDescriptor)
        return propertyDescriptor
    }

    private fun createChildProperties(
            genericType: GenericType<*>,
            addedDescriptors: MutableMap<GenericType<*>, PropertyDescriptor>,
            addedReferences: MutableMap<GenericType<*>, MutableList<PropertyDescriptorReference>>,
            nestedTypes: Set<GenericType<*>>
    ): List<Property> {
        return rawPropertyCollectors.flatMap { collector ->
            genericTypeHierarchy.hierarchy(genericType).flatMap { collector.forType(it) }.asIterable()
        }.map({ rawProperty -> mapProperty(rawProperty, addedDescriptors, addedReferences, nestedTypes) })
    }

    private fun mapProperty(
            rawProperty: RawProperty,
            addedDescriptors: MutableMap<GenericType<*>, PropertyDescriptor>,
            addedReferences: MutableMap<GenericType<*>, MutableList<PropertyDescriptorReference>>,
            nestedTypes: Set<GenericType<*>>
    ): Property {
        return from(rawProperty.name, rawProperty.genericType, rawProperty.annotations,
                rawProperty.valueAccessor, addedDescriptors, addedReferences, nestedTypes)
    }

    companion object {
        private val ROOT_NAME = "#"

        private fun rootValueAccessor(instance: Any): Any {
            throw IllegalStateException("cannot call value accessor for root element")
        }
    }
}
