package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.SchemaContext
import com.mercateo.jsonschema.SchemaGenerator
import com.mercateo.jsonschema.generictype.GenericType
import com.mercateo.jsonschema.property.mapper.PropertyMapper

class PropertyBuilderWrapper(
    private val propertyBuilder: PropertyBuilder,
    private vararg val propertyMappers: PropertyMapper
) {

    fun <T> from(
        genericType: GenericType<T>,
        schemaContext: SchemaContext = SchemaContext(SchemaGenerator.defaultPropertyChecker)
    ): Property<Void, T> {
        var property = propertyBuilder.from(genericType)

        for (propertyMapper in propertyMappers) {
            property = propertyMapper.from(property, schemaContext)
        }

        return property
    }
}

fun <T> PropertyBuilderWrapper.from(clazz: Class<T>): Property<Void, T> {
    return from(GenericType.of(clazz))
}

