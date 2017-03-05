package com.mercateo.jsonschema.schema

import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.property.RawPropertyCollector
import java.util.Objects.requireNonNull

data class SchemaPropertyContext(
        val propertyChecker: PropertyChecker,
        val unwrapAnnotations: List<Class<out Annotation>>,
        val propertyCollectors: List<RawPropertyCollector>
) {

    fun isApplicable(property: Property<*, *>): Boolean {
        return propertyChecker.test(requireNonNull(property))
    }
}