package com.mercateo.jsonschema.mapper

import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.schema.ObjectContext

class SchemaMapper(private val referencedElementCollector: ReferencedElementCollector = ReferencedElementCollector()) {

    fun <T> toJson(context: ObjectContext<T>): ObjectNode {
        val referencedElements = referencedElementCollector.collectReferencedElements(context.property)

        return SchemaPropertyMapper(referencedElements).toJson(context)
    }
}
