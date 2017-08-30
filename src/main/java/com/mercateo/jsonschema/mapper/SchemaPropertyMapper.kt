package com.mercateo.jsonschema.mapper

import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.type.*
import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.property.PropertyType
import com.mercateo.jsonschema.mapper.ObjectContext
import com.mercateo.jsonschema.schema.mapper.*

class SchemaPropertyMapper(private val referencedElements: Set<String>) {

    internal var nodeFactory = ObjectNodeFactory.nodeFactory

    private val primitivePropertyMappers = mapOf(Pair(PropertyType.STRING, StringJsonPropertyMapper(nodeFactory)),
            Pair(PropertyType.INTEGER, IntegerJsonPropertyMapper(nodeFactory)),
            Pair(PropertyType.NUMBER, NumberJsonPropertyMapper(nodeFactory)),
            Pair(PropertyType.BOOLEAN, BooleanJsonPropertyMapper(nodeFactory)),
            Pair(PropertyType.ARRAY, ArrayJsonPropertyMapper(this, nodeFactory)),
            Pair(PropertyType.OBJECT, ObjectJsonPropertyMapper(this, nodeFactory))
    )

    fun <T> toJson(context: ObjectContext<T>): ObjectNode {

        val propertyDescriptor = context.propertyDescriptor

        if (context.reference != null) {
            val result = nodeFactory.objectNode()
            result.put("\$ref", context.reference)
            return result
        } else {
            val propertyNode = primitivePropertyMappers.get(propertyDescriptor.propertyType)!!.toJson(context)
            val name = context.property.path

            if (referencedElements.contains(name)) {
                propertyNode.put("id", name)
            }

            return propertyNode
        }

    }

}
