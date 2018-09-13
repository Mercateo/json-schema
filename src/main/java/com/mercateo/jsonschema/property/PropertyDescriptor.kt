package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType

data class PropertyDescriptor<T>(
        val propertyType: PropertyType,
        val genericType: GenericType<T>,
        val context: PropertyDescriptor.Context,
        val annotations: Map<Class<out Annotation>, Set<Annotation>>,
        val polymorphicSubTypes: List<Property<T, Any>> = emptyList()
) {
    @Suppress("UNCHECKED_CAST")
    val children: List<Property<T, Any>> =
            when (context) {
                is PropertyDescriptor.Context.Children<*> -> context.children as List<Property<T, Any>>
                else -> emptyList()
            }

    val innerReference: Boolean =
            when (context) {
                is PropertyDescriptor.Context.InnerReference -> true
                else -> false
            }

    sealed class Context {
        class Children<T>(val children: List<Property<T, Any>>) : Context()
        object InnerReference : Context()
    }
}