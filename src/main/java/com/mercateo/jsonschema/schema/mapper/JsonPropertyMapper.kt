package com.mercateo.jsonschema.schema.mapper

import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.schema.JsonProperty

internal interface JsonPropertyMapper {
    fun toJson(jsonProperty: JsonProperty): ObjectNode
}
