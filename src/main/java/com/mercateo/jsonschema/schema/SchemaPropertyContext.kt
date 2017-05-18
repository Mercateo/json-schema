package com.mercateo.jsonschema.schema

import com.fasterxml.jackson.databind.deser.impl.MethodProperty
import com.mercateo.jsonschema.property.MethodCollector
import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.property.RawPropertyCollector
import java.util.Objects.requireNonNull

data class SchemaPropertyContext(
        val propertyChecker: PropertyChecker = object : PropertyChecker {
            override fun test(t: Property<*, *>): Boolean {
                return true
            }
        },
        val unwrapAnnotations: List<Class<out Annotation>> = emptyList(),
        val propertyCollectors: List<RawPropertyCollector> = listOf(MethodCollector())
) {

    fun isApplicable(property: Property<*, *>): Boolean {
        return propertyChecker.test(requireNonNull(property))
    }
}