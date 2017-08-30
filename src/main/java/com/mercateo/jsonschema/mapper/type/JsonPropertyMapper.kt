package com.mercateo.jsonschema.mapper.type

import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.ObjectContext

internal interface JsonPropertyMapper {
    fun toJson(property: ObjectContext<*>): ObjectNode
}
