package com.mercateo.jsonschema.mapper.type

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.ObjectContext

internal class GenericJsonPropertyMapper(private val nodeFactory: JsonNodeFactory) {

    fun <T> addDefaultAndAllowedValues(
        propertyNode: ObjectNode, property: ObjectContext<T>,
        nodeCreator: (T) -> JsonNode
    ) {
        if (hasDefaultValue(property)) {
            propertyNode.set<ObjectNode>("default", getDefaultValue(property, nodeCreator))
        }
        if (hasAllowedValues(property)) {
            propertyNode.set<ObjectNode>("enum", getAllowedValues(property, nodeCreator))
        }
    }

    private fun hasAllowedValues(jsonProperty: ObjectContext<*>): Boolean {
        return jsonProperty.allowedValues.isNotEmpty()
    }

    private fun <T> getAllowedValues(jsonProperty: ObjectContext<T>, nodeCreator: (T) -> JsonNode): ArrayNode {
        val arrayNode = ArrayNode(nodeFactory)
        jsonProperty.allowedValues.map(nodeCreator).forEach { arrayNode.add(it) }
        return arrayNode
    }

    private fun hasDefaultValue(jsonProperty: ObjectContext<*>): Boolean {
        val defaultValue = jsonProperty.defaultValue
        return defaultValue != null
    }

    private fun <T> getDefaultValue(jsonProperty: ObjectContext<T>, nodeCreator: (T) -> JsonNode): JsonNode {
        return nodeCreator.invoke(jsonProperty.defaultValue!!)
    }

}
