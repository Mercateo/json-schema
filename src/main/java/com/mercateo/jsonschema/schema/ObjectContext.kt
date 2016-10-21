package com.mercateo.jsonschema.schema

data class ObjectContext<out T> (
        val allowedValues : List<T>,
        val defaultValue : T
)
