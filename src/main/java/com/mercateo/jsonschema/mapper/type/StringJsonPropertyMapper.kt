package com.mercateo.jsonschema.mapper.type

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.mercateo.jsonschema.mapper.ObjectContext
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

internal class StringJsonPropertyMapper(nodeFactory: JsonNodeFactory) : JsonPropertyMapper {

    private val primitiveJsonPropertyBuilder: PrimitiveJsonPropertyBuilder = PrimitiveJsonPropertyBuilder(nodeFactory)

    override fun toJson(property: ObjectContext<*>): ObjectNode {
        val enum = property.propertyDescriptor.genericType.isEnum

        val nodeCreator: (Any) -> JsonNode =
            if (enum) {
                { value -> TextNode((value as Enum<*>).name) }
            } else {
                { value -> TextNode(value as String) }
            }

        val propertyNodeBuilder = primitiveJsonPropertyBuilder
            .forProperty(property as ObjectContext<String>)
            .withType("string")
            .withDefaultAndAllowedValues(nodeCreator)

        if (enum) {
            propertyNodeBuilder.withAllowedValuesDefault(
                property.propertyDescriptor.genericType.rawType.enumConstants as Array<Enum<*>>, nodeCreator
            )
        }

        val propertyNode = propertyNodeBuilder.build()

        val sizeAnnotations = property.property.annotations[Size::class.java] ?: emptySet()
        sizeAnnotations
            .fold(Int.MAX_VALUE) { max, ann -> if ((ann as Size).max < max) ann.max else max }
            .takeIf { it < Int.MAX_VALUE }
            ?.let { propertyNode.put("maxLength", it) }

        sizeAnnotations
            .fold(0) { min, ann -> if ((ann as Size).min > min) ann.min else min }
            .takeIf { it > 0 }
            ?.let { propertyNode.put("minLength", it) }

        if (sizeAnnotations.isEmpty() && !(property.property.annotations[NotEmpty::class.java] ?: emptySet()).isEmpty()) {
            propertyNode.put("minLength", 1)
        }

        val patternAnnotations = property.property.annotations[Pattern::class.java]
        patternAnnotations
            ?.map { (it as Pattern).regexp }
            ?.firstOrNull()
            ?.let { propertyNode.put("pattern", it) }

        return propertyNode
    }
}
