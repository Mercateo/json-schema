package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType

class PropertyBuilderWrapper(private val propertyBuilder: PropertyBuilder, vararg propertyMappers: PropertyMapper) : PropertyBuilder {

    private val propertyMappers: Array<out PropertyMapper>

    init {
        this.propertyMappers = propertyMappers
    }

    override fun from(propertyClass: Class<*>): Property {
        return from(GenericType.Companion.of(propertyClass))
    }

    override fun from(genericType: GenericType<*>): Property {
        var property = propertyBuilder.from(genericType)

        for (propertyMapper in propertyMappers) {
            property = propertyMapper.from(property)
        }

        return property
    }
}


