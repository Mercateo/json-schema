package com.mercateo.jsonschema

import com.mercateo.jsonschema.mapper.PropertyChecker
import com.mercateo.jsonschema.property.Property


data class SchemaContext(
    val propertyChecker: PropertyChecker = object : PropertyChecker {
        override fun test(t: Property<*, *>): Boolean = true
    }
)
