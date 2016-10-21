package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType

data class Property(
        val name: String,
        val propertyDescriptor: PropertyDescriptor,
        val valueAccessor: (Any) -> Any?,
        val annotations: Map<Class<out Annotation>, Set<Annotation>>
) {
    fun getValue(instance: Any): Any? {
        return valueAccessor(instance)
    }

    val children: List<Property> =
            propertyDescriptor.children

    val genericType: GenericType<*> =
            propertyDescriptor.genericType

    val propertyType: PropertyType =
            propertyDescriptor.propertyType
}
