package com.mercateo.jsonschema.mapper.type.internal

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.ObjectContext
import com.mercateo.jsonschema.mapper.SchemaPropertyMapper
import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.property.PropertyDescriptor
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull


class DefaultObjectMapper(
    private val nodeFactory: JsonNodeFactory,
    private val schemaPropertyMapper: SchemaPropertyMapper
) {

    fun <T> addStandardObjectSchema(
        variant: PropertyDescriptor.Variant.Properties<T>,
        properties: ObjectContext<T>,
        propertyNode: ObjectNode
    ) {
        val objectNode = ObjectNode(nodeFactory)
        for (property in variant.children) {
            objectNode.set<ObjectNode>(
                property.name, schemaPropertyMapper.toJson(
                    properties.createInner(property, property.valueAccessor)
                )
            )
        }
        propertyNode.set<ObjectNode>("properties", objectNode)

        addRequiredElements(properties, propertyNode)
    }

    private fun addRequiredElements(properties: ObjectContext<*>, propertyNode: ObjectNode) {
        val arrayNode = ArrayNode(nodeFactory)

        properties.propertyDescriptor.children.filter(this::isRequired).forEach { arrayNode.add(it.name) }

        if (arrayNode.size() > 0) {
            propertyNode.set<ObjectNode>("required", arrayNode)
        }
    }

    private fun isRequired(property: Property<*, *>): Boolean {
        val annotations = property.annotations
        val keys = annotations.keys
        return keys.intersect(setOf(NotNull::class.java, NotEmpty::class.java)).isNotEmpty()
    }
}