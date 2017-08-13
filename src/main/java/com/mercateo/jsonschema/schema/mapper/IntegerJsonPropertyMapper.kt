package com.mercateo.jsonschema.schema.mapper

import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.schema.JsonProperty

internal class IntegerJsonPropertyMapper(nodeFactory: JsonNodeFactory) : JsonPropertyMapper {

    private val primitiveJsonPropertyBuilder: PrimitiveJsonPropertyBuilder

    init {
        primitiveJsonPropertyBuilder = PrimitiveJsonPropertyBuilder(nodeFactory)
    }

    override fun toJson(jsonProperty: JsonProperty): ObjectNode {
        val nodeCreator = { value: Any -> IntNode(value as Int) }
        val propertyNode = primitiveJsonPropertyBuilder.forProperty(jsonProperty)
                .withType("integer").withDefaultAndAllowedValues(nodeCreator).build()
        val valueConstraints = jsonProperty.valueConstraints
        valueConstraints.min?.let { propertyNode.put("minimum", it) }
        valueConstraints.max?.let { propertyNode.put("maximum", it) }
        return propertyNode
    }
}
