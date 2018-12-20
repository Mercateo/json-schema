package com.mercateo.jsonschema.mapper.type

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.ObjectContext
import com.mercateo.jsonschema.mapper.SchemaPropertyMapper
import com.mercateo.jsonschema.mapper.type.internal.DefaultObjectMapper
import com.mercateo.jsonschema.mapper.type.internal.PolymorphicObjectMapper
import com.mercateo.jsonschema.property.PropertyDescriptor

internal class ObjectJsonPropertyMapper(
        schemaPropertyMapper: SchemaPropertyMapper ,
        private val nodeFactory: JsonNodeFactory
) : JsonPropertyMapper {

    val defaultObjectMapper: DefaultObjectMapper

    val polymorphicObjectMapper: PolymorphicObjectMapper

    init {
        defaultObjectMapper = DefaultObjectMapper(nodeFactory, schemaPropertyMapper)

        polymorphicObjectMapper = PolymorphicObjectMapper(nodeFactory, schemaPropertyMapper)
    }

    override fun toJson(property: ObjectContext<*>): ObjectNode {
        val propertyNode = ObjectNode(nodeFactory)

        propertyNode.put("type", "object")

        val variant = property.propertyDescriptor.variant

        when (variant) {
            is PropertyDescriptor.Variant.Properties<*> -> {
                defaultObjectMapper.addStandardObjectSchema(variant as PropertyDescriptor.Variant.Properties<Any>, property as ObjectContext<Any>, propertyNode)
            }
            is PropertyDescriptor.Variant.Polymorphic -> {
                polymorphicObjectMapper.addPolymorphicObjectSchema(variant, property as ObjectContext<Any>, propertyNode)
            }
        }

        return propertyNode
    }


}
