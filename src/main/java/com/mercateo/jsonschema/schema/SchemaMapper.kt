package com.mercateo.jsonschema.schema

import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.ObjectNodeFactory.Companion.nodeFactory
import com.mercateo.jsonschema.property.PropertyType
import com.mercateo.jsonschema.schema.mapper.*


private val primitivePropertyMappers = mapOf(Pair(PropertyType.STRING, StringJsonPropertyMapper(nodeFactory)),
        Pair(PropertyType.INTEGER, IntegerJsonPropertyMapper(nodeFactory)),
        Pair(PropertyType.NUMBER, NumberJsonPropertyMapper(nodeFactory)),
        Pair(PropertyType.BOOLEAN, BooleanJsonPropertyMapper(nodeFactory)))


class PropertyJsonSchemaMapper {

    /**
     * Convert the property hierarchy given in `property` to a JSON
     * string.

     * @param jsonProperty
     * *            property hierarchy to be converted
     * *
     * @return JSONObject representing the schema of the given property
     * *         hierarchy
     */
    fun toJson(jsonProperty: JsonPropertyResult): ObjectNode {
        return PropertyJsonSchemaMapperForRoot(jsonProperty).toJson(jsonProperty.root)
    }
}

class PropertyJsonSchemaMapperForRoot(jsonProperty: JsonPropertyResult) {

    val referencedElements: Set<JsonProperty> = jsonProperty.referencedElements

    private val objectPropertyMappers: Map<PropertyType, JsonPropertyMapper> = mapOf(
            Pair(PropertyType.OBJECT, ObjectJsonPropertyMapper(this, nodeFactory)),
            Pair(PropertyType.ARRAY, ArrayJsonPropertyMapper(this, nodeFactory))
    )

    fun toJson(jsonProperty: JsonProperty): ObjectNode {
        if (jsonProperty.ref != null) {
            val propertyNode = createObjectNode()
            propertyNode.put("\$ref", jsonProperty.ref)
            return propertyNode
        } else {
            val jsonPropertyMapper: JsonPropertyMapper
            val propertyType = jsonProperty.type

            if (primitivePropertyMappers.containsKey(propertyType)) {
                jsonPropertyMapper = primitivePropertyMappers[propertyType]!!
            } else {
                val objectPropertyType = if (objectPropertyMappers.containsKey(propertyType))
                    propertyType
                else
                    PropertyType.OBJECT
                jsonPropertyMapper = objectPropertyMappers[objectPropertyType]!!
            }

            val propertyNode = jsonPropertyMapper.toJson(jsonProperty)
            if (referencedElements.contains(jsonProperty)) {
                propertyNode.put("id", jsonProperty.path)
            }
            return propertyNode
        }
    }


    private fun createObjectNode(): ObjectNode {
        return ObjectNode(nodeFactory)
    }
}