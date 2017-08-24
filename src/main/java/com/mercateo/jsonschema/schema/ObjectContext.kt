package com.mercateo.jsonschema.schema

import com.mercateo.jsonschema.property.Property

data class ObjectContext<T>(
        val property: Property<*, T>,
        val defaultValue: T? = null,
        val allowedValues: List<T> = emptyList()
) {
    val propertyDescriptor get() = property.propertyDescriptor
    val reference get() = property.reference
}
