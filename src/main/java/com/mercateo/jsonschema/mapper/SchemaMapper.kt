package com.mercateo.jsonschema.mapper

import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.property.PropertyDescriptorReference
import com.mercateo.jsonschema.property.PropertyType

class SchemaMapper {

    internal var objectNodeFactory = ObjectNodeFactory()

    fun map(property: Property): ObjectNode {
        val result = objectNodeFactory.createNode()

        val propertyDescriptor = property.propertyDescriptor

        if (propertyDescriptor is PropertyDescriptorReference) {

            result.put("\$ref", propertyDescriptor.reference)
            return result
        } else {
            when (propertyDescriptor.propertyType) {
                PropertyType.OBJECT -> {
                }
            }
        }

        return result
    }
}
