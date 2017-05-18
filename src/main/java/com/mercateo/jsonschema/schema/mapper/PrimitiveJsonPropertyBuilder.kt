package com.mercateo.jsonschema.schema.mapper

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.schema.JsonProperty

import java.util.function.Function

internal class PrimitiveJsonPropertyBuilder(private val nodeFactory: JsonNodeFactory) {
    private val genericJsonPropertyMapper: GenericJsonPropertyMapper

    init {
        this.genericJsonPropertyMapper = GenericJsonPropertyMapper(nodeFactory)
    }

    fun forProperty(jsonProperty: JsonProperty): Builder {
        return Builder(jsonProperty)
    }

    internal inner class Builder(private val jsonProperty: JsonProperty) {
        private val propertyNode: ObjectNode

        init {
            propertyNode = ObjectNode(nodeFactory)
        }

        fun withType(type: String): Builder {
            propertyNode.put("type", type)
            return this
        }

        fun withDefaultAndAllowedValues(nodeCreator: (Any) -> JsonNode): Builder {
            genericJsonPropertyMapper.addDefaultAndAllowedValues(propertyNode, this.jsonProperty, nodeCreator)
            return this
        }

        fun withDefaultValue(value: JsonNode): Builder {
            propertyNode.set("default", value)
            return this
        }

        fun build(): ObjectNode {
            return propertyNode
        }

    }
}
