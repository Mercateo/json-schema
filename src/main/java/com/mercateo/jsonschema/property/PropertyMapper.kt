package com.mercateo.jsonschema.property

interface PropertyMapper {
    fun <S, T> from(property: Property<S, T>): Property<S, T>
}
