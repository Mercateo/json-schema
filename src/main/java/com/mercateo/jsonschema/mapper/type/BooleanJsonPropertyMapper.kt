package com.mercateo.jsonschema.mapper.type

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.ObjectContext

internal class BooleanJsonPropertyMapper(nodeFactory: JsonNodeFactory) : JsonPropertyMapper {

    private val primitiveJsonPropertyBuilder: PrimitiveJsonPropertyBuilder

    init {
        primitiveJsonPropertyBuilder = PrimitiveJsonPropertyBuilder(nodeFactory)
    }

    override fun toJson(property: ObjectContext<*>): ObjectNode {
        val nodeCreator: (Boolean) -> JsonNode = {
            if (it)
                BooleanNode.TRUE
            else
                BooleanNode.FALSE
        }

        return primitiveJsonPropertyBuilder.forProperty(property as ObjectContext<Boolean>)
                .withType("boolean")
                .withDefaultValue(BooleanNode.FALSE)
                .withDefaultAndAllowedValues(nodeCreator).build()
    }

}
