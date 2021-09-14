package com.mercateo.jsonschema.property.mapper

import com.mercateo.jsonschema.SchemaContext
import com.mercateo.jsonschema.collections.MutablePropertyDescriptorMap
import com.mercateo.jsonschema.generictype.GenericType
import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.property.PropertyDescriptor
import com.mercateo.jsonschema.property.PropertyDescriptor.Variant.Properties
import java.util.concurrent.ConcurrentHashMap

class UnwrappedPropertyMapper
    (vararg unwrapProperties: UnwrappedPropertyUpdater<*>) : PropertyMapper {

    private val unwrappers: Map<Class<out Annotation>, UnwrappedPropertyUpdater<*>> =
        unwrapProperties.groupBy { it.annotation }.mapValues { it.value.first() }

    private val unwrappedProperties: ConcurrentHashMap<GenericType<*>, Property<*, *>> =
        ConcurrentHashMap<GenericType<*>, Property<*, *>>()

    private val unwrappedPropertyDescriptors = MutablePropertyDescriptorMap()

    override fun <S, T> from(property: Property<S, T>, schemaContext: SchemaContext): Property<S, T> {
        @Suppress("UNCHECKED_CAST")
        return unwrappedProperties.computeIfAbsent(property.genericType) { unwrap(property) } as Property<S, T>
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
        val propertyDescriptor =
            PropertyDescriptor(propertyType, genericType, Properties(children), property.propertyDescriptor.annotations)
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
            .sortedBy { it.name }
    }

    private fun <T> unwrapChildrenIfApplicable(child: Property<T, Any>): List<Property<T, Any>> {
        return child.annotations.keys.firstOrNull { unwrappers.keys.contains(it) }
            ?.let { Pair(child.annotations[it]!!.first(), unwrappers[it]!! as UnwrappedPropertyUpdater<Annotation>) }
            ?.let { { name: String -> it.second.updateName(name, it.first) } }
            ?.let { unwrapChildren(child, it) }
            ?: listOf(child)
    }

    private fun <T, U> unwrapChildren(child: Property<T, U>, unwrapper: (String) -> String): List<Property<T, Any>> {
        return child.children.map { mapUnwrappedProperty(child, it, unwrapper) }
    }

    private fun <T, U, V> mapUnwrappedProperty(
        property: Property<T, U>,
        child: Property<U, V>,
        unwrapper: (String) -> String
    ): Property<T, V> {
        val valueAccessor: (T) -> V? = { instance ->
            val intermediateObject: U? = property.valueAccessor(instance)
            val intermediateValueAccessor: (U) -> V? = child.valueAccessor
            if (intermediateObject != null) intermediateValueAccessor(intermediateObject) else null
        }
        return Property(unwrapper.invoke(child.name), child.propertyDescriptor, valueAccessor, child.annotations)
    }
}

abstract class UnwrappedPropertyUpdater<T : Annotation>(
    val annotation: Class<T>
) {
    abstract fun updateName(name: String, annotation: T): String
}
