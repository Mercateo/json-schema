package com.mercateo.jsonschema.schema.mapper

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.schema.JsonProperty
import com.mercateo.jsonschema.schema.PropertyJsonSchemaMapperForRoot

internal class ArrayJsonPropertyMapper(
        private val propertyJsonSchemaMapper: PropertyJsonSchemaMapperForRoot,
        private val nodeFactory: JsonNodeFactory
) : JsonPropertyMapper {

    override fun toJson(jsonProperty: JsonProperty): ObjectNode {
        val propertyNode = ObjectNode(nodeFactory)
        propertyNode.put("type", "array")
        propertyNode.set("items", propertyJsonSchemaMapper .toJson(jsonProperty.properties[0]))
        jsonProperty .sizeConstraints .min?.let { propertyNode.put("minItems", it) }
        jsonProperty .sizeConstraints .max?.let { propertyNode.put("maxItems", it) }
        return propertyNode
    }
}
