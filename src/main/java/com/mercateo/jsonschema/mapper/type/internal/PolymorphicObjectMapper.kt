package com.mercateo.jsonschema.mapper.type.internal

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.ObjectContext
import com.mercateo.jsonschema.mapper.SchemaPropertyMapper
import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.property.PropertyDescriptor

class PolymorphicObjectMapper(
        private val nodeFactory: JsonNodeFactory,
        private val schemaPropertyMapper: SchemaPropertyMapper
) {

    fun addPolymorphicObjectSchema(variant: PropertyDescriptor.Variant.Polymorphic, properties: ObjectContext<Any>, objectNode: ObjectNode) {
        objectNode.set("anyOf", createPossibleTypeSchemas(variant.elements, properties))
    }

    private fun createPossibleTypeSchemas(polymorphicTypes: List<Property<Any, Any>>, properties: ObjectContext<Any>): ArrayNode {
        val subtypeSchemas = ArrayNode(nodeFactory)

        for (subType in polymorphicTypes) {
            val subtypeSchema = createSubTypeSchema(subType, properties)
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
