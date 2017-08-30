package com.mercateo.jsonschema.mapper.type

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.DoubleNode
import com.fasterxml.jackson.databind.node.FloatNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.ObjectContext

internal class NumberJsonPropertyMapper(nodeFactory: JsonNodeFactory) : JsonPropertyMapper {

    private val primitiveJsonPropertyBuilder: PrimitiveJsonPropertyBuilder

    init {
        primitiveJsonPropertyBuilder = PrimitiveJsonPropertyBuilder(nodeFactory)
    }

    override fun toJson(jsonProperty: ObjectContext<*>): ObjectNode {
        return primitiveJsonPropertyBuilder.forProperty(jsonProperty as ObjectContext<Number>) //
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
