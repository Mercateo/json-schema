package com.mercateo.jsonschema.mapper.type

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.ObjectContext

internal class BooleanJsonPropertyMapper(nodeFactory: JsonNodeFactory) : JsonPropertyMapper {

    private val primitiveJsonPropertyBuilder: PrimitiveJsonPropertyBuilder = PrimitiveJsonPropertyBuilder(nodeFactory)

    override fun toJson(property: ObjectContext<*>): ObjectNode {
        val nodeCreator: (Boolean) -> JsonNode = {
            if (it)
                BooleanNode.TRUE
            else
                BooleanNode.FALSE
        }

        @Suppress("UNCHECKED_CAST")
        return primitiveJsonPropertyBuilder.forProperty(property as ObjectContext<Boolean>)
            .withType("boolean")
            .withDefaultAndAllowedValues(nodeCreator).build()
    }

}
