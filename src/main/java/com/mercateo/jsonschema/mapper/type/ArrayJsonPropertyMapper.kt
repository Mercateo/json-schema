package com.mercateo.jsonschema.mapper.type

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.ObjectContext
import com.mercateo.jsonschema.mapper.SchemaPropertyMapper

internal class ArrayJsonPropertyMapper(
        private val schemaPropertyMapper: SchemaPropertyMapper,
        private val nodeFactory: JsonNodeFactory
) : JsonPropertyMapper {

    override fun toJson(property: ObjectContext<*>): ObjectNode {
        val propertyNode = ObjectNode(nodeFactory)
        propertyNode.put("type", "array")
        propertyNode.set("items", schemaPropertyMapper.toJson(ObjectContext(property.property.children[0])));
        /*jsonProperty.sizeConstraints.min?.let { propertyNode.put("minItems", it) }
        jsonProperty.sizeConstraints.max?.let { propertyNode.put("maxItems", it) }*/
        return propertyNode;
    }
}
