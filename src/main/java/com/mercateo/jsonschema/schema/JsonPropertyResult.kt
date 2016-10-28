package com.mercateo.jsonschema.schema

data class JsonPropertyResult(
        val root: JsonProperty,
        val referencedElements: Set<JsonProperty>
)
