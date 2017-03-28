package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType

interface PropertyDescriptor<T> {
    val propertyType: PropertyType
    val genericType: GenericType<T>
    val context: Context
    val annotations: Map<Class<out Annotation>, Set<Annotation>>
    val children: List<Property<T, Any>>

    sealed class Context {
        class Children<T>(val children: List<Property<T, Any>>) : Context()
        object InnerReference : Context()
    }
}