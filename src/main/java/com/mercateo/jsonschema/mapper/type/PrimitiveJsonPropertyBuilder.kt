package com.mercateo.jsonschema.mapper.type

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.mercateo.jsonschema.mapper.ObjectContext

internal class PrimitiveJsonPropertyBuilder(private val nodeFactory: JsonNodeFactory) {
    private val genericJsonPropertyMapper: GenericJsonPropertyMapper

    init {
        this.genericJsonPropertyMapper = GenericJsonPropertyMapper(nodeFactory)
    }

    fun <T> forProperty(jsonProperty: ObjectContext<T>): Builder<T> {
        return Builder(jsonProperty)
    }

    internal inner class Builder<T>(private val jsonProperty: ObjectContext<T>) {
        private val propertyNode: ObjectNode

        init {
            propertyNode = ObjectNode(nodeFactory)
        }

        fun withType(type: String): Builder<T> {
            propertyNode.put("type", type)
            return this
        }

        fun withDefaultAndAllowedValues(nodeCreator: (T) -> JsonNode): Builder<T> {
            genericJsonPropertyMapper.addDefaultAndAllowedValues(propertyNode, this.jsonProperty, nodeCreator)
            return this
        }

        fun withAllowedValuesDefault(enumConstants: Array<Enum<*>>, nodeCreator: (Any) -> JsonNode): Builder<T> {
            if (!propertyNode.has("enum")) {
                val arrayNode = ArrayNode(nodeFactory)
                enumConstants.map(nodeCreator).forEach({ arrayNode.add(it) })
                propertyNode.set<ObjectNode>("enum", arrayNode)
            }
            return this
        }

        fun build(): ObjectNode {
            return propertyNode
        }

    }
}
