package com.mercateo.jsonschema.schema.mapper

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.schema.JsonProperty

internal class GenericJsonPropertyMapper(private val nodeFactory: JsonNodeFactory) {

    fun addDefaultAndAllowedValues(propertyNode: ObjectNode, property: JsonProperty,
                                   nodeCreator: (Any) -> JsonNode) {
        if (hasDefaultValue(property)) {
            propertyNode.set("default", getDefaultValue(property, nodeCreator))
        }
        if (hasAllowedValues(property)) {
            propertyNode.set("enum", getAllowedValues(property, nodeCreator))
        }
    }

    private fun hasAllowedValues(jsonProperty: JsonProperty): Boolean {
        return !jsonProperty.allowedValues.isEmpty()
    }

    private fun getAllowedValues(jsonProperty: JsonProperty, nodeCreator: (Any) -> JsonNode): ArrayNode {
        val arrayNode = ArrayNode(nodeFactory)
        jsonProperty.allowedValues.stream().map(nodeCreator).forEach({ arrayNode.add(it) })
        return arrayNode
    }

    private fun hasDefaultValue(jsonProperty: JsonProperty): Boolean {
        val defaultValue = jsonProperty.defaultValue
        return defaultValue != null
    }

    private fun getDefaultValue(jsonProperty: JsonProperty, nodeCreator: (Any) -> JsonNode): JsonNode {
        return nodeCreator.invoke(jsonProperty.defaultValue!!)
    }

}
