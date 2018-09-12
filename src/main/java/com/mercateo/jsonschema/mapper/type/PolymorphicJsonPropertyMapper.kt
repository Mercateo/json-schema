package com.mercateo.jsonschema.mapper.type;

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.ObjectContext
import com.mercateo.jsonschema.mapper.SchemaPropertyMapper
import com.mercateo.jsonschema.property.Property

internal class PolymorphicJsonPropertyMapper(
        private val schemaPropertyMapper: SchemaPropertyMapper,
        private val nodeFactory: JsonNodeFactory
) : JsonPropertyMapper {

    override fun toJson(property: ObjectContext<*>): ObjectNode {
        return toJsonInner(property)
    }

    fun <T> toJsonInner(property: ObjectContext<T>): ObjectNode {
        val propertyNode = ObjectNode(nodeFactory)
        propertyNode.put("type", "object")
        propertyNode.set("anyOf", createPossibleTypeSchemas(property))
        return propertyNode
    }

    private fun <T> createPossibleTypeSchemas(context: ObjectContext<T>): ArrayNode {
        val subtypeSchemas = ArrayNode(nodeFactory)
        for (subType in context.propertyDescriptor.polymorphicSubTypes) {
            val subtypeSchema = createSubTypeSchema(subType, context)
            val augmentedSubtypeSchema = augmentSchema(subType.name, subtypeSchema)
            subtypeSchemas.add(augmentedSubtypeSchema)
        }
        return subtypeSchemas
    }

    private fun <T> createSubTypeSchema(subType: Property<T, Any>, context: ObjectContext<T>): ObjectNode {
        val polyProperty = context.createInner(subType, subType.valueAccessor)
        return schemaPropertyMapper.toJson(polyProperty)
    }

    private fun augmentSchema(typeAlias: String, subtypeSchema: ObjectNode): ObjectNode {
        val enum = ArrayNode(nodeFactory)
        enum.add(typeAlias)

        val type = ObjectNode(nodeFactory).apply {
            put("type", "string")
            set("enum", enum)
        }

        subtypeSchema.get("properties").apply {
            if (this is ObjectNode) {
                set("@type", type)
            }
        }

        // create @type field as enumeration in the specific sub-schema
        // and allow only the corresponding value
        return subtypeSchema
    }
}