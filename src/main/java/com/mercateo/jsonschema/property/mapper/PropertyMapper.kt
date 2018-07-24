package com.mercateo.jsonschema.property.mapper

import com.mercateo.jsonschema.SchemaContext
import com.mercateo.jsonschema.property.Property

interface PropertyMapper {
    fun <S, T> from(property: Property<S, T>, schemaContext: SchemaContext): Property<S, T>
}
