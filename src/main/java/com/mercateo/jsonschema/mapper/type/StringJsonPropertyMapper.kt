package com.mercateo.jsonschema.mapper.type

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.mercateo.jsonschema.mapper.ObjectContext

internal class StringJsonPropertyMapper(nodeFactory: JsonNodeFactory) : JsonPropertyMapper {

    private val primitiveJsonPropertyBuilder: PrimitiveJsonPropertyBuilder

    init {
        primitiveJsonPropertyBuilder = PrimitiveJsonPropertyBuilder(nodeFactory)
    }

    override fun toJson(jsonProperty: ObjectContext<*>): ObjectNode {
        val nodeCreator: (String) -> JsonNode = { value -> TextNode(value) }
        val propertyNode = primitiveJsonPropertyBuilder.forProperty(jsonProperty as ObjectContext<String>).withType("string").withDefaultAndAllowedValues(nodeCreator).build()
        /*jsonProperty.sizeConstraints.min?.let { propertyNode.put("minLength", it) }
        jsonProperty.sizeConstraints.min?.let { propertyNode.put("maxLength", it) }*/
        return propertyNode
    }
}
