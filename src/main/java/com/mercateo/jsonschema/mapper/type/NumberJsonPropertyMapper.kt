package com.mercateo.jsonschema.mapper.type

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.DoubleNode
import com.fasterxml.jackson.databind.node.FloatNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.ObjectContext

internal class NumberJsonPropertyMapper(nodeFactory: JsonNodeFactory) : JsonPropertyMapper {

    private val primitiveJsonPropertyBuilder: PrimitiveJsonPropertyBuilder = PrimitiveJsonPropertyBuilder(nodeFactory)

    override fun toJson(property: ObjectContext<*>): ObjectNode {
        @Suppress("UNCHECKED_CAST")
        return primitiveJsonPropertyBuilder.forProperty(property as ObjectContext<Number>) //
            .withType("number").withDefaultAndAllowedValues(this::createNode).build()
    }

    private fun createNode(value: Number): JsonNode {
        return when (value) {
            is Float -> FloatNode(value)
            is Double -> DoubleNode(value)
            else -> throw IllegalStateException("cannot create Number node for unknown type")
        }
    }
}
