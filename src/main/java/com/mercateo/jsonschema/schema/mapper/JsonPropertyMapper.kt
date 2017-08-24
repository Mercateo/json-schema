package com.mercateo.jsonschema.schema.mapper

import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.schema.ObjectContext

internal interface JsonPropertyMapper {
    fun toJson(property: ObjectContext<*>): ObjectNode
}
