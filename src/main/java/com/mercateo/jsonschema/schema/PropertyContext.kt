package com.mercateo.jsonschema.schema

import java.util.*

data class PropertyContext<out T>(
        val allowedValues: Set<T> = emptySet(),
        val defaultValue: T? = null) {

    fun <U> createInner(valueAccessor: (T) -> U?): PropertyContext<U> {

        @Suppress("UNCHECKED_CAST")
        val allowedValues = allowedValues.map(valueAccessor).filter(
                Objects::nonNull).toSet() as Set<U>

        val defaultValue = defaultValue?.let(valueAccessor)

        return PropertyContext<U>(allowedValues, defaultValue)
    }
}

