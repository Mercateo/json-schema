package com.mercateo.jsonschema.schema.mapper

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.schema.ObjectContext

internal class PrimitiveJsonPropertyBuilder(private val nodeFactory: JsonNodeFactory) {
    private val genericJsonPropertyMapper: GenericJsonPropertyMapper

    init {
        this.genericJsonPropertyMapper = GenericJsonPropertyMapper(nodeFactory)
    }

    fun <T> forProperty(jsonProperty: ObjectContext<T>): Builder<T> {
        return Builder(jsonProperty)
    }

    internal inner class Builder<T>(private val jsonProperty: ObjectContext<T>) {
        private val propertyNode: ObjectNode

        init {
            propertyNode = ObjectNode(nodeFactory)
        }

        fun withType(type: String): Builder<T> {
            propertyNode.put("type", type)
            return this
        }

        fun withDefaultAndAllowedValues(nodeCreator: (T) -> JsonNode): Builder<T> {
            genericJsonPropertyMapper.addDefaultAndAllowedValues(propertyNode, this.jsonProperty, nodeCreator)
            return this
        }

        fun withDefaultValue(value: JsonNode): Builder<T> {
            propertyNode.set("default", value)
            return this
        }

        fun build(): ObjectNode {
            return propertyNode
        }

    }
}
