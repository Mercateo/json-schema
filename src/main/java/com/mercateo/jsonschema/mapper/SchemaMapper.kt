package com.mercateo.jsonschema.mapper

import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.property.PropertyType
import com.mercateo.jsonschema.schema.ObjectContext
import com.mercateo.jsonschema.schema.mapper.*

class SchemaMapper {

    internal var nodeFactory = ObjectNodeFactory.nodeFactory

    val referencedElements: Set<Property<*, *>> = emptySet()

    private val primitivePropertyMappers = mapOf(Pair(PropertyType.STRING, StringJsonPropertyMapper(nodeFactory)),
            Pair(PropertyType.INTEGER, IntegerJsonPropertyMapper(nodeFactory)),
            Pair(PropertyType.NUMBER, NumberJsonPropertyMapper(nodeFactory)),
            Pair(PropertyType.BOOLEAN, BooleanJsonPropertyMapper(nodeFactory)),
            Pair(PropertyType.ARRAY, ArrayJsonPropertyMapper(this, nodeFactory)),
            Pair(PropertyType.OBJECT, ObjectJsonPropertyMapper(this, nodeFactory))
    )

    fun <T> toJson(context: ObjectContext<T>): ObjectNode {
        val result = nodeFactory.objectNode()

        val propertyDescriptor = context.propertyDescriptor

        if (context.reference != null) {
            result.put("\$ref", context.reference)
            return result
        } else {
            val propertyNode = primitivePropertyMappers.get(propertyDescriptor.propertyType)!!.toJson(context)
            if (referencedElements.contains(context.property)) {
                propertyNode.put("id", context.property.path)
            }

            return propertyNode
        }

    }

}
