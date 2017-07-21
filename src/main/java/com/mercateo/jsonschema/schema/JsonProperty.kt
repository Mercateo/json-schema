package com.mercateo.jsonschema.schema

import com.mercateo.jsonschema.property.PropertyType

data class JsonProperty(
        val id: String,
        val name: String,
        val type: PropertyType,
        val ref: String?,
        val defaultValue: String?,
        val isRequired: Boolean = false,
        val properties: List<JsonProperty>,
        val sizeConstraints: SizeConstraints,
        val valueConstraints: ValueConstraints,
        val allowedValues: Set<String>,
        val path: String
)

