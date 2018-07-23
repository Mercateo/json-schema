package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType

class PropertyBuilderWrapper(private val propertyBuilder: PropertyBuilder, vararg propertyMappers: PropertyMapper) : PropertyBuilder {

    private val propertyMappers: Array<out PropertyMapper> = propertyMappers

    override fun <T> from(genericType: GenericType<T>): Property<Void, T> {
        var property = propertyBuilder.from(genericType)

        for (propertyMapper in propertyMappers) {
            property = propertyMapper.from(property)
        }

        return property
    }
}


