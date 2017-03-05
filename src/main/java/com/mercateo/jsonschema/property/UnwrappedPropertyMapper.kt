package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.collections.MutablePropertyDescriptorMap
import com.mercateo.jsonschema.generictype.GenericType
import java.util.concurrent.ConcurrentHashMap

class UnwrappedPropertyMapper
constructor(vararg unwrapAnnotations: Class<out Annotation>) : PropertyMapper {

    private val unwrapAnnotations: Set<Class<out Annotation>>

    private val unwrappedProperties: ConcurrentHashMap<GenericType<*>, Property<*, *>>

    private val unwrappedPropertyDescriptors = MutablePropertyDescriptorMap()

    init {
        this.unwrapAnnotations = setOf(*unwrapAnnotations)

        this.unwrappedProperties = ConcurrentHashMap<GenericType<*>, Property<*, *>>()
    }

    override fun <S, T> from(property: Property<S, T>): Property<S, T> {
        @Suppress("UNCHECKED_CAST")
        return unwrappedProperties.computeIfAbsent(property.genericType, { unwrap(property) }) as Property<S, T>
    }

    private fun <S, T> unwrap(property: Property<S, T>): Property<S, T> {
        val addedUnwrappedPropertyDescriptors = MutablePropertyDescriptorMap()
        val unwrappedProperty = unwrap(property, addedUnwrappedPropertyDescriptors)
        unwrappedPropertyDescriptors.putAll(addedUnwrappedPropertyDescriptors)
        return unwrappedProperty
    }

    private fun <S, T> unwrap(
            property: Property<S, T>,
            addedUnwrappedProperties: MutablePropertyDescriptorMap
    ): Property<S, T> {
        val genericType = property.genericType

        val propertyDescriptor =
                if (unwrappedPropertyDescriptors.containsKey(genericType)) {
                    unwrappedPropertyDescriptors[genericType]
                } else {
                    if (addedUnwrappedProperties.containsKey(genericType)) {
                        addedUnwrappedProperties[genericType]
                    } else {
                        createDescriptor(property, addedUnwrappedProperties)
                    }
                }
        return Property(property.name, propertyDescriptor, property.valueAccessor, property.annotations)
    }

    private fun <T> createDescriptor(
            property: Property<*, T>,
            addedUnwrappedProperties: MutablePropertyDescriptorMap
    ): PropertyDescriptor<T> {
        val children = getChildren(property, addedUnwrappedProperties)
        val propertyType = property.propertyType
        val genericType = property.genericType
        val propertyDescriptor = PropertyDescriptorDefault(propertyType, genericType, PropertyDescriptor.Context.Children(children), property.propertyDescriptor.annotations)
        addedUnwrappedProperties.put(genericType, propertyDescriptor)
        return propertyDescriptor
    }

    private fun <S, T> getChildren(
            property: Property<S, T>,
            addedUnwrappedPropertyDescriptors: MutablePropertyDescriptorMap
    ): List<Property<T, Any>> {
        return property.children
                .map { unwrap(it, addedUnwrappedPropertyDescriptors) }
                .flatMap(this::unwrapChildrenIfApplicable)
    }

    private fun <T> unwrapChildrenIfApplicable(child: Property<T, Any>): List<Property<T, Any>> {
            val doUnwrapChild: Boolean = !unwrapAnnotations.intersect(child.annotations.keys).isEmpty()

            return if (doUnwrapChild) {
                unwrapChildren(child)
            } else {
                listOf(child)
            }
    }

    private fun <T, U> unwrapChildren(child: Property<T, U>): List<Property<T, Any>> {
        return child.children.map({ mapUnwrappedProperty(child, it) })
    }

    private fun <T, U, V> mapUnwrappedProperty(property: Property<T, U>, child: Property<U, V>): Property<T, V> {
        val valueAccessor: (T) -> V? = { instance ->
            val intermediateObject: U? = property.valueAccessor(instance)
            val intermediateValueAccessor: (U) -> V? = child.valueAccessor
            if (intermediateObject != null) intermediateValueAccessor(intermediateObject) else null
        }
        return Property(child.name, child.propertyDescriptor, valueAccessor, child.annotations)
    }
}
