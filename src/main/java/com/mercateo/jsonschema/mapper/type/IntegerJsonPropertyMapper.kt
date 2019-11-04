package com.mercateo.jsonschema.mapper.type

import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.ObjectContext
import javax.validation.constraints.Max
import javax.validation.constraints.Min

internal class IntegerJsonPropertyMapper(nodeFactory: JsonNodeFactory) : JsonPropertyMapper {

    private val primitiveJsonPropertyBuilder: PrimitiveJsonPropertyBuilder

    init {
        primitiveJsonPropertyBuilder = PrimitiveJsonPropertyBuilder(nodeFactory)
    }

    override fun toJson(property: ObjectContext<*>): ObjectNode {
        val nodeCreator = { value: Int -> IntNode(value) }
        @Suppress("UNCHECKED_CAST") val propertyNode = primitiveJsonPropertyBuilder.forProperty(property as ObjectContext<Int>)
                .withType("integer").withDefaultAndAllowedValues(nodeCreator).build()

        property.property.annotations.get(Max::class.java)
                ?.fold(Long.MAX_VALUE, { max, ann -> if ((ann as Max).value < max) ann.value else max })
                ?.let { propertyNode.put("maximum", it) }

        property.property.annotations.get(Min::class.java)
                ?.fold(Long.MIN_VALUE, { min, ann -> if ((ann as Min).value > min) ann.value else min })
                ?.let { propertyNode.put("minimum", it) }

        return propertyNode
    }
}
