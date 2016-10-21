package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType

interface PropertyBuilder {
    fun from(propertyClass: Class<*>): Property

    fun from(genericType: GenericType<*>): Property
}
