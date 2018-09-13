package com.mercateo.jsonschema.property

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.mercateo.jsonschema.collections.MutablePropertyDescriptorMap
import com.mercateo.jsonschema.generictype.GenericType
import com.mercateo.jsonschema.generictype.GenericTypeHierarchy
import com.mercateo.jsonschema.mapper.Polymorphic
import com.mercateo.jsonschema.mapper.TypeValue
import com.mercateo.jsonschema.property.annotation.AnnotationMapBuilder
import java.util.*

class BasicPropertyBuilder(
        customUnwrappers: Map<Class<*>, (Any) -> Any?> = emptyMap(),
        private val rawPropertyCollectors: List<RawPropertyCollector> = emptyList(),
        polymorphic: Polymorphic? = null
) : PropertyBuilder {

    private val customUnwrappers: Map<Class<*>, (Any) -> Any?>

    private val polymorphic: Polymorphic

    init {
        this.customUnwrappers = customUnwrappers + Pair(Optional::class.java, { option ->
            if (option is Optional<*>) {
                option.orElse(null)
            } else {
                null
            }
        })

        this.polymorphic = polymorphic ?: Polymorphic()

    }

    private val genericTypeHierarchy: GenericTypeHierarchy = GenericTypeHierarchy()

    private val annotationMapBuilder: AnnotationMapBuilder = AnnotationMapBuilder()

    private val knownDescriptors: MutablePropertyDescriptorMap = MutablePropertyDescriptorMap()

    override fun <T> from(genericType: GenericType<T>): Property<Void, T> {
        return from(ROOT_NAME, genericType, mutableMapOf(),
                { it: Void -> rootValueAccessor(it) }, mutableSetOf())
    }

    private fun <S, T> from(
            name: String,
            genericType: GenericType<T>,
            annotations: Map<Class<out Annotation>, Set<Annotation>>,
            valueAccessor: (S) -> T?,
            nestedTypes: Set<GenericType<*>>): Property<S, T> {
        val addedDescriptors = MutablePropertyDescriptorMap()
        val property = from(name, genericType, annotations, valueAccessor, addedDescriptors, nestedTypes)
        knownDescriptors.putAll(addedDescriptors)

        return property
    }

    private fun <S, T> from(
            name: String,
            genericType: GenericType<T>,
            annotations: Map<Class<out Annotation>, Set<Annotation>>,
            valueAccessor: (S) -> T?,
            addedDescriptors: MutablePropertyDescriptorMap,
            nestedTypes: Set<GenericType<*>>): Property<S, T> {
        val propertyDescriptor = getPropertyDescriptor(genericType, addedDescriptors, nestedTypes)

        val mergedAnnotations = annotationMapBuilder.merge(annotations, propertyDescriptor.annotations)
        return Property(name, propertyDescriptor, valueAccessor, mergedAnnotations)
    }

    private fun <T> getPropertyDescriptor(
            genericType: GenericType<T>,
            addedDescriptors: MutablePropertyDescriptorMap,
            nestedTypes: Set<GenericType<*>>): PropertyDescriptor<T> {
        if (knownDescriptors.containsKey(genericType)) {
            return knownDescriptors[genericType]
        } else {
            if (addedDescriptors.containsKey(genericType)) {
                return addedDescriptors[genericType]
            } else {
                if (!nestedTypes.contains(genericType)) {
                    return createPropertyDescriptor(genericType, addedDescriptors, nestedTypes)
                } else {
                    val propertyType = PropertyTypeMapper.of(genericType)
                    val annotations = genericType.rawType.annotations.toList()
                    return PropertyDescriptor(propertyType, genericType, PropertyDescriptor.Variant.Reference,
                            annotationMapBuilder.createMap(annotations))
                }
            }
        }
    }

    private fun <T> createPropertyDescriptor(
            genericType: GenericType<T>,
            addedDescriptors: MutablePropertyDescriptorMap,
            nestedTypes: Set<GenericType<*>>
    ): PropertyDescriptor<T> {
        val propertyType = PropertyTypeMapper.of(genericType)

        val variant: PropertyDescriptor.Variant = when (propertyType) {
            PropertyType.OBJECT -> {
                createObjectPropertiesHandlingPolymorphicTypes(genericType as GenericType<Any>, addedDescriptors, nestedTypes)
            }

            PropertyType.ARRAY -> {
                val containedType = genericType.containedType as GenericType<Any>
                val typeValue = TypeValue(containedType as GenericType<Any>, "")
                PropertyDescriptor.Variant.Properties<T>(listOf(from("", containedType, mutableMapOf(), { o -> null }, addedDescriptors, nestedTypes)))
            }

            else -> PropertyDescriptor.Variant.Primitive
        }

        val annotations = genericType.rawType.annotations.filter { !it.annotationClass.qualifiedName!!.startsWith("kotlin.") }
        val propertyDescriptor = PropertyDescriptor(propertyType, genericType, variant, annotationMapBuilder.createMap(annotations))
        addedDescriptors.put(genericType, propertyDescriptor)
        return propertyDescriptor
    }

    private fun createObjectPropertiesHandlingPolymorphicTypes(genericType: GenericType<Any>, addedDescriptors: MutablePropertyDescriptorMap, nestedTypes: Set<GenericType<*>>): PropertyDescriptor.Variant {
        return if (polymorphic.isPolymorphic(genericType)) {
            PropertyDescriptor.Variant.Polymorphic(
                    createPolymorphicProperty(genericType, addedDescriptors, nestedTypes))
        } else {
            PropertyDescriptor.Variant.Properties(
                    createChildProperties(genericType, addedDescriptors, nestedTypes + genericType))
        }
    }

    private fun <T> createPolymorphicProperty(
            genericType: GenericType<Any>,
            addedDescriptors: MutablePropertyDescriptorMap,
            nestedTypes: Set<GenericType<*>>
    ): List<Property<T, Any>> {
        return polymorphic
                .getSubTypes(genericType)
                .map {
                    val name = it.name;
                    val type = it.type
                    val annotations = genericType.rawType.annotations.filter { !it.annotationClass.qualifiedName!!.startsWith("kotlin.") }
                    val valueAccessor: (T) -> Any? = { throw IllegalStateException("should not happen!") }
                    from(name, type, annotationMapBuilder.createMap(annotations), valueAccessor, addedDescriptors, nestedTypes)
                }
    }

    private fun <T> readSubtypesFromAnnotations(genericType: GenericType<T>): Map<String, Class<*>> {
        val jsonSubTypes = genericType.rawType.getAnnotation(JsonSubTypes::class.java)

        return jsonSubTypes.value.map { it.name to it.value.java }.toMap()
    }

    private fun <T> createChildProperties(
            genericType: GenericType<T>,
            addedDescriptors: MutablePropertyDescriptorMap,
            nestedTypes: Set<GenericType<*>>
    ): List<Property<T, Any>> {
        return rawPropertyCollectors.flatMap { collector ->
            genericTypeHierarchy
                    .hierarchy(genericType)
                    .flatMap { collector.forType(it) }
                    .map {
                        it as RawProperty<T, Any>
                    }
                    .sortedBy { it.name }
                    .asIterable()
        }.map { mapProperty(it, addedDescriptors, nestedTypes) }
    }

    private fun <S, T> mapProperty(
            rawProperty: RawProperty<S, T>,
            addedDescriptors: MutablePropertyDescriptorMap,
            nestedTypes: Set<GenericType<*>>
    ): Property<S, Any> {
        val genericType = rawProperty.genericType
        val customUnwrapper = customUnwrappers.get(genericType.rawType)

        val targetGenericType = (if (customUnwrapper == null) genericType else {
            genericType.containedType
        }) as GenericType<Any>

        val valueAccessor: (S) -> Any? = if (customUnwrapper == null) rawProperty.valueAccessor else { it: S ->
            val inner = rawProperty.valueAccessor.invoke(it)
            if (inner != null) customUnwrapper.invoke(inner) else null
        }
        return from(rawProperty.name,
                targetGenericType,
                if (customUnwrapper == null) rawProperty.annotations else emptyMap(),
                valueAccessor, addedDescriptors, nestedTypes)
    }

    companion object {
        private val ROOT_NAME = "#"

        private fun <T> rootValueAccessor(instance: Void): T? {
            throw IllegalStateException("cannot call value accessor for root element")
        }
    }
}
