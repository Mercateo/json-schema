package com.mercateo.jsonschema.schema.mapper

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.DoubleNode
import com.fasterxml.jackson.databind.node.FloatNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.schema.JsonProperty

internal class NumberJsonPropertyMapper(nodeFactory: JsonNodeFactory) : JsonPropertyMapper {

    private val primitiveJsonPropertyBuilder: PrimitiveJsonPropertyBuilder

    init {
        primitiveJsonPropertyBuilder = PrimitiveJsonPropertyBuilder(nodeFactory)
    }

    override fun toJson(jsonProperty: JsonProperty): ObjectNode {
        return primitiveJsonPropertyBuilder.forProperty(jsonProperty) //
                .withType("number").withDefaultAndAllowedValues { value: Any -> createNode(value) }.build()
    }

    private fun createNode(value: Any): JsonNode {
        return when (value) {
            is Float -> FloatNode(value)
            is Double -> DoubleNode(value)
            else -> throw IllegalStateException("cannot create Number node for unknown type")
        }
    }
}
