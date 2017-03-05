package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType

interface PropertyBuilder {
    fun <T> from(propertyClass: Class<T>): Property<Void, T>

    fun <T> from(genericType: GenericType<T>): Property<Void, T>
}
