package com.mercateo.jsonschema.schema.mapper

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.schema.JsonProperty

internal class BooleanJsonPropertyMapper(nodeFactory: JsonNodeFactory) : JsonPropertyMapper {

    private val primitiveJsonPropertyBuilder: PrimitiveJsonPropertyBuilder

    init {
        primitiveJsonPropertyBuilder = PrimitiveJsonPropertyBuilder(nodeFactory)
    }

    override fun toJson(jsonProperty: JsonProperty): ObjectNode {
        val nodeCreator: (Boolean) -> JsonNode = {
            if (it)
                BooleanNode.TRUE
            else
                BooleanNode.FALSE
        }

        return primitiveJsonPropertyBuilder.forProperty(jsonProperty)
                .withType("boolean")
                .withDefaultValue(BooleanNode.FALSE)
                .withDefaultAndAllowedValues(nodeCreator as (Any) -> JsonNode).build()
    }

}
