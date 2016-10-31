package com.mercateo.jsonschema.property

interface PropertyMapper {
    fun from(property: Property): Property
}
