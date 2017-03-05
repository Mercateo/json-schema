package com.mercateo.jsonschema.mapper

import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.property.PropertyDescriptor
import com.mercateo.jsonschema.property.PropertyType

class SchemaMapper {

    internal var objectNodeFactory = ObjectNodeFactory()

    fun <S, T> map(property: Property<S, T>): ObjectNode {
        val result = objectNodeFactory.createNode()

        val propertyDescriptor = property.propertyDescriptor

        when (propertyDescriptor.context) {
            is PropertyDescriptor.Context.Children<*> -> {
                when (propertyDescriptor.propertyType) {
                    PropertyType.OBJECT -> {
                    }
                }
            }
            is PropertyDescriptor.Context.InnerReference -> {
                // should not happen
            }
        }

        property.reference.let { reference ->
            result.put("\$ref", reference)
        }

        return result
    }
}
