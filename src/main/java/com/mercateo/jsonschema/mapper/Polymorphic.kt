package com.mercateo.jsonschema.mapper

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.mercateo.jsonschema.generictype.GenericType


class Polymorphic {
    fun isPolymorphic(genericType: GenericType<Any>): Boolean {
        val annotation = genericType.rawType.getAnnotation(JsonSubTypes::class.java)
        return annotation != null
    }

    fun getSubTypes(genericType: GenericType<Any>): List<TypeValue<Any>> {
        val jsonSubTypes = genericType.rawType.getAnnotation(JsonSubTypes::class.java)
        return jsonSubTypes?.value?.map {
            TypeValue(name = it.name, type = GenericType.of(it.value))
        }
            ?: listOf(TypeValue(name = genericType.name, type = genericType))
    }
}

data class TypeValue<T>(
    val type: GenericType<T>,
    val name: String
)