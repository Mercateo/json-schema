package com.mercateo.jsonschema.mapper.type

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.ObjectContext
import com.mercateo.jsonschema.mapper.SchemaPropertyMapper
import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.property.PropertyDescriptor
import javax.validation.constraints.NotNull

internal class ObjectJsonPropertyMapper(
        private val schemaPropertyMapper: SchemaPropertyMapper
        ,
        private val nodeFactory: JsonNodeFactory
) : JsonPropertyMapper {

    override fun toJson(properties: ObjectContext<*>): ObjectNode {
        val propertyNode = ObjectNode(nodeFactory)
        propertyNode.put("type", "object")


        val variant = properties.propertyDescriptor.variant

        when (variant) {
            is PropertyDescriptor.Variant.Properties<*> -> {
                addStandardObjectSchema(variant as PropertyDescriptor.Variant.Properties<Any>, properties as ObjectContext<Any>, propertyNode)
            }
            is PropertyDescriptor.Variant.Polymorphic -> {
                addPolymorphicObjectSchema(variant, properties as ObjectContext<Any>, propertyNode)
            }
        }

        return propertyNode
    }

    private fun addPolymorphicObjectSchema(variant: PropertyDescriptor.Variant.Polymorphic, properties: ObjectContext<Any>, objectNode: ObjectNode) {
        objectNode.set("anyOf", createPossibleTypeSchemas(variant.elements, properties))
    }

    private fun <T> addStandardObjectSchema(variant: PropertyDescriptor.Variant.Properties<T>, properties: ObjectContext<T>, propertyNode: ObjectNode) {
        val objectNode = ObjectNode(nodeFactory)
        for (property in variant.children) {
            objectNode.set(property.name, schemaPropertyMapper.toJson(
                    properties.createInner(property, property.valueAccessor)
            ))
        }
        propertyNode.set("properties", objectNode)

        addRequiredElements(properties, propertyNode)
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

    fun addRequiredElements(properties: ObjectContext<*>, propertyNode: ObjectNode) {
        val arrayNode = ArrayNode(nodeFactory)

        properties.propertyDescriptor.children.filter(this::isRequired).forEach { arrayNode.add(it.name) }
        val requiredElements = arrayNode

        if (requiredElements.size() > 0) {
            propertyNode.set("required", requiredElements)
        }
    }

    private fun isRequired(property: Property<*, *>): Boolean {
        val annotations = property.annotations
        return annotations.containsKey(NotNull::class.java)
    }
}
