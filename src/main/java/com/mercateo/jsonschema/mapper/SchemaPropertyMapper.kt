package com.mercateo.jsonschema.mapper

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.type.*
import com.mercateo.jsonschema.property.PropertyType

class SchemaPropertyMapper(private val referencedElements: Set<String>) {

    private val propertyMappers = mapOf(
        Pair(PropertyType.STRING, StringJsonPropertyMapper(nodeFactory)),
        Pair(PropertyType.INTEGER, IntegerJsonPropertyMapper(nodeFactory)),
        Pair(PropertyType.NUMBER, NumberJsonPropertyMapper(nodeFactory)),
        Pair(PropertyType.BOOLEAN, BooleanJsonPropertyMapper(nodeFactory)),
        Pair(PropertyType.ARRAY, ArrayJsonPropertyMapper(this, nodeFactory)),
        Pair(PropertyType.OBJECT, ObjectJsonPropertyMapper(this, nodeFactory))
    )

    fun <T> toJson(context: ObjectContext<T>): ObjectNode {

        return if (context.reference != null) {
            nodeFactory.objectNode().apply { put("\$ref", context.reference) }
        } else {
            propertyMappers[context.propertyDescriptor.propertyType]!!.toJson(context).apply {
                val name = context.property.path

                if (referencedElements.contains(name)) {
                    put("id", name)
                }
            }
        }
    }

    companion object {
        internal val nodeFactory = JsonNodeFactory(true)
    }
}
