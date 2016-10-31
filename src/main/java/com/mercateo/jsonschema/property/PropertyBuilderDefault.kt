package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType
import com.mercateo.jsonschema.generictype.GenericTypeHierarchy
import java.util.concurrent.ConcurrentHashMap

class PropertyBuilderDefault(vararg rawPropertyCollectors: RawPropertyCollector) : PropertyBuilder {

    private val rawPropertyCollectors: Array<out RawPropertyCollector>

    private val genericTypeHierarchy: GenericTypeHierarchy

    private val annotationMapBuilder: AnnotationMapBuilder

    private val knownDescriptors: ConcurrentHashMap<GenericType<*>, PropertyDescriptor>

    init {
        this.rawPropertyCollectors = rawPropertyCollectors
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
        val property = from(name, genericType, annotations, valueAccessor, addedDescriptors, nestedTypes)
        knownDescriptors.putAll(addedDescriptors)

        return property
    }

    private fun from(
            name: String,
            genericType: GenericType<*>,
            annotations: Map<Class<out Annotation>, Set<Annotation>>,
            valueAccessor: (Any) -> Any?,
            addedDescriptors: MutableMap<GenericType<*>, PropertyDescriptor>,
            nestedTypes: Set<GenericType<*>>): Property {
        val propertyDescriptor = getPropertyDescriptor(genericType, addedDescriptors, nestedTypes)

        return Property(name, propertyDescriptor, valueAccessor, annotationMapBuilder.merge(annotations, propertyDescriptor.annotations))
    }

    private fun getPropertyDescriptor(
            genericType: GenericType<*>,
            addedDescriptors: MutableMap<GenericType<*>, PropertyDescriptor>,
            nestedTypes: Set<GenericType<*>>): PropertyDescriptor {
        if (knownDescriptors.containsKey(genericType)) {
            return knownDescriptors[genericType]!!
        } else {
            if (addedDescriptors.containsKey(genericType)) {
                return addedDescriptors[genericType]!!
            } else {
                if (!nestedTypes.contains(genericType)) {
                    return createPropertyDescriptor(genericType, addedDescriptors, nestedTypes)
                } else {
                    val propertyType = PropertyTypeMapper.of(genericType)
                    val annotations = genericType.rawType.annotations
                    return PropertyDescriptor(propertyType, genericType, PropertyDescriptor.Context.InnerReference,
                            annotationMapBuilder.createMap(*annotations))
                }
            }
        }
    }

    private fun createPropertyDescriptor(
            genericType: GenericType<*>,
            addedDescriptors: MutableMap<GenericType<*>, PropertyDescriptor>,
            nestedTypes: Set<GenericType<*>>
    ): PropertyDescriptor {
        val propertyType = PropertyTypeMapper.of(genericType)

        val children = when (propertyType) {
            PropertyType.OBJECT -> createChildProperties(genericType, addedDescriptors, nestedTypes + genericType)

            PropertyType.ARRAY ->
                listOf(from("", genericType.containedType, mutableMapOf(), { o -> null }, addedDescriptors, nestedTypes))

            else -> emptyList()
        }

        val annotations = genericType.rawType.annotations.filter { !it.annotationClass.qualifiedName!!.startsWith("kotlin.") }.toTypedArray()
        val propertyDescriptor = PropertyDescriptor(propertyType, genericType, PropertyDescriptor.Context.Children(children), annotationMapBuilder.createMap(*annotations))
        addedDescriptors.put(genericType, propertyDescriptor)
        return propertyDescriptor
    }

    private fun createChildProperties(
            genericType: GenericType<*>,
            addedDescriptors: MutableMap<GenericType<*>, PropertyDescriptor>,
            nestedTypes: Set<GenericType<*>>
    ): List<Property> {
        return rawPropertyCollectors.flatMap { collector ->
            genericTypeHierarchy.hierarchy(genericType).flatMap { collector.forType(it) }.asIterable()
        }.map({ rawProperty -> mapProperty(rawProperty, addedDescriptors, nestedTypes) })
    }

    private fun mapProperty(
            rawProperty: RawProperty,
            addedDescriptors: MutableMap<GenericType<*>, PropertyDescriptor>,
            nestedTypes: Set<GenericType<*>>
    ): Property {
        return from(rawProperty.name, rawProperty.genericType, rawProperty.annotations,
                rawProperty.valueAccessor, addedDescriptors, nestedTypes)
    }

    companion object {
        private val ROOT_NAME = "#"

        private fun rootValueAccessor(instance: Any): Any {
            throw IllegalStateException("cannot call value accessor for root element")
        }
    }
}
