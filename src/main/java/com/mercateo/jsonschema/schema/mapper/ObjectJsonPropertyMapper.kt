package com.mercateo.jsonschema.schema.mapper

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.SchemaMapper
import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.schema.ObjectContext

internal class ObjectJsonPropertyMapper(
        private val propertyJsonSchemaMapper: SchemaMapper
        ,
        private val nodeFactory: JsonNodeFactory
) : JsonPropertyMapper {

    override fun toJson(jsonProperty: ObjectContext<*>): ObjectNode {
        val propertyNode = ObjectNode(nodeFactory)
        propertyNode.put("type", "object")
        propertyNode.set("properties", createProperties(jsonProperty))
        val requiredElements = createRequiredElementsArray(jsonProperty.propertyDescriptor.children)
        if (requiredElements.size() > 0) {
            propertyNode.set("required", requiredElements)
        }
        return propertyNode
    }

    private fun <T> createProperties(properties: ObjectContext<T>): ObjectNode {
        val result = ObjectNode(nodeFactory)
        for (property in properties.propertyDescriptor.children) {
            val child = properties.createInner(property, property.valueAccessor)
            result.set(property.name, propertyJsonSchemaMapper.toJson(child))
        }
        return result
    }

    private fun createRequiredElementsArray(properties: List<Property<Nothing, Any>>): ArrayNode {
        val result = ArrayNode(nodeFactory)
        /*for ((_, name, _, _, _, isRequired) in properties) {
            if (isRequired) {
                result.add(name)
            }
        }*/
        return result
    }
}
