package com.mercateo.jsonschema.schema.mapper

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.SchemaMapper
import com.mercateo.jsonschema.schema.ObjectContext

internal class ArrayJsonPropertyMapper(
        private val propertyJsonSchemaMapper: SchemaMapper,
        private val nodeFactory: JsonNodeFactory
) : JsonPropertyMapper {

    override fun toJson(property: ObjectContext<*>): ObjectNode {
        val propertyNode = ObjectNode(nodeFactory)
        propertyNode.put("type", "array")
        propertyNode.set("items", propertyJsonSchemaMapper.toJson(ObjectContext(property.property.children[0])));
        /*jsonProperty.sizeConstraints.min?.let { propertyNode.put("minItems", it) }
        jsonProperty.sizeConstraints.max?.let { propertyNode.put("maxItems", it) }*/
        return propertyNode;
    }
}
