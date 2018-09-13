package com.mercateo.jsonschema.property.mapper

import com.mercateo.jsonschema.SchemaContext
import com.mercateo.jsonschema.property.Property


class CheckedPropertyMapper : PropertyMapper {
    override fun <S, T> from(property: Property<S, T>, schemaContext: SchemaContext): Property<S, T> {
        return property.filterChildren { schemaContext.propertyChecker.test(it) }
    }
}