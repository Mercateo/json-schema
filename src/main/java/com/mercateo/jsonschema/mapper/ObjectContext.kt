package com.mercateo.jsonschema.mapper

import com.mercateo.jsonschema.property.Property
import java.util.*

data class ObjectContext<T>(
        val property: Property<*, T>,
        val defaultValue: T? = null,
        val allowedValues: Set<T> = emptySet()
) {
    val propertyDescriptor get() = property.propertyDescriptor

    val reference get() = property.reference

    fun <U> createInner(child: Property<T, U>, valueAccessor: (T) -> U?): ObjectContext<U> {

        @Suppress("UNCHECKED_CAST")
        val allowedValues = allowedValues
                .map(valueAccessor)
                .filter(Objects::nonNull)
                .toSet() as Set<U>

        val defaultValue = defaultValue?.let(valueAccessor)

        return ObjectContext<U>(child, defaultValue, allowedValues)
    }
}
