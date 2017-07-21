package com.mercateo.jsonschema.schema.mapper

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.schema.JsonProperty
import com.mercateo.jsonschema.schema.PropertyJsonSchemaMapperForRoot

internal class ObjectJsonPropertyMapper(
        private val propertyJsonSchemaMapper: PropertyJsonSchemaMapperForRoot,
        private val nodeFactory: JsonNodeFactory
) : JsonPropertyMapper {

    override fun toJson(jsonProperty: JsonProperty): ObjectNode {
        val propertyNode = ObjectNode(nodeFactory)
        propertyNode.put("type", "object")
        propertyNode.set("properties", createProperties(jsonProperty.properties))
        val requiredElements = createRequiredElementsArray(jsonProperty.properties)
        if (requiredElements.size() > 0) {
            propertyNode.set("required", requiredElements)
        }
        return propertyNode
    }

    private fun createProperties(properties: List<JsonProperty>): ObjectNode {
        val result = ObjectNode(nodeFactory)
        for (jsonProperty in properties) {
            result.set(jsonProperty.name, propertyJsonSchemaMapper.toJson(jsonProperty))
        }
        return result
    }

    private fun createRequiredElementsArray(properties: List<JsonProperty>): ArrayNode {
        val result = ArrayNode(nodeFactory)
        for ((_, name, _, _, _, isRequired) in properties) {
            if (isRequired) {
                result.add(name)
            }
        }
        return result
    }
}
