package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.collections.MutablePropertyDescriptorMap
import com.mercateo.jsonschema.generictype.GenericType
import com.mercateo.jsonschema.generictype.GenericTypeHierarchy

class PropertyBuilderDefault(vararg rawPropertyCollectors: RawPropertyCollector) : PropertyBuilder {

    private val rawPropertyCollectors: Array<out RawPropertyCollector>

    private val genericTypeHierarchy: GenericTypeHierarchy

    private val annotationMapBuilder: AnnotationMapBuilder

    private val knownDescriptors: MutablePropertyDescriptorMap

    init {
        this.rawPropertyCollectors = rawPropertyCollectors
        this.genericTypeHierarchy = GenericTypeHierarchy()
        this.annotationMapBuilder = AnnotationMapBuilder()
        this.knownDescriptors = MutablePropertyDescriptorMap()
    }

    override fun <T> from(propertyClass: Class<T>): Property<Void, T> {
        return from(GenericType.of(propertyClass))
    }

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

        return Property(name, propertyDescriptor, valueAccessor, annotationMapBuilder.merge(annotations, propertyDescriptor.annotations))
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
                    val annotations = genericType.rawType.annotations
                    return PropertyDescriptorDefault(propertyType, genericType, PropertyDescriptor.Context.InnerReference,
                            annotationMapBuilder.createMap(*annotations))
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

        val children: List<Property<T, Any>> = when (propertyType) {
            PropertyType.OBJECT -> createChildProperties(genericType, addedDescriptors, nestedTypes + genericType)

            PropertyType.ARRAY ->
                listOf(from("", genericType.containedType as GenericType<Any>, mutableMapOf(), { o -> null }, addedDescriptors, nestedTypes))

            else -> emptyList()
        }

        val annotations = genericType.rawType.annotations.filter { !it.annotationClass.qualifiedName!!.startsWith("kotlin.") }.toTypedArray()
        val propertyDescriptor = PropertyDescriptorDefault(propertyType, genericType, PropertyDescriptor.Context.Children(children), annotationMapBuilder.createMap(*annotations))
        addedDescriptors.put(genericType, propertyDescriptor)
        return propertyDescriptor
    }

    private fun <T> createChildProperties(
            genericType: GenericType<T>,
            addedDescriptors: MutablePropertyDescriptorMap,
            nestedTypes: Set<GenericType<*>>
    ): List<Property<T, Any>> {
        return rawPropertyCollectors.flatMap { collector ->
            genericTypeHierarchy.hierarchy(genericType).flatMap { collector.forType(it) }.map {
                it as RawProperty<T, Any>
            }.asIterable()
        }.map { mapProperty(it, addedDescriptors, nestedTypes) }
    }

    private fun <S, T> mapProperty(
            rawProperty: RawProperty<S, T>,
            addedDescriptors: MutablePropertyDescriptorMap,
            nestedTypes: Set<GenericType<*>>
    ): Property<S, T> {
        return from(rawProperty.name, rawProperty.genericType, rawProperty.annotations,
                rawProperty.valueAccessor, addedDescriptors, nestedTypes)
    }

    companion object {
        private val ROOT_NAME = "#"

        private fun <T> rootValueAccessor(instance: Void): T? {
            throw IllegalStateException("cannot call value accessor for root element")
        }
    }
}