package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType

import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID

object PropertyTypeMapper {

    private val TYPE_MAP = mapOf(
        Pair(String::class.java, PropertyType.STRING),
        Pair(Boolean::class.java, PropertyType.BOOLEAN),
        Pair(java.lang.Boolean.TYPE, PropertyType.BOOLEAN),
        Pair(Int::class.java, PropertyType.INTEGER),
        Pair(Integer.TYPE, PropertyType.INTEGER),
        Pair(Long::class.java, PropertyType.INTEGER),
        Pair(java.lang.Long.TYPE, PropertyType.INTEGER),
        Pair(Float::class.java, PropertyType.NUMBER),
        Pair(java.lang.Float.TYPE, PropertyType.NUMBER),
        Pair(Double::class.java, PropertyType.NUMBER),
        Pair(java.lang.Double.TYPE, PropertyType.NUMBER),
        Pair(BigInteger::class.java, PropertyType.INTEGER),
        Pair(BigDecimal::class.java, PropertyType.NUMBER),
        Pair(UUID::class.java, PropertyType.STRING))

    fun of(type: GenericType<*>): PropertyType {
        if (type.isIterable) {
            return PropertyType.ARRAY
        }

        val clazz = type.rawType

        if (Enum::class.java.isAssignableFrom(clazz)) {
            return PropertyType.STRING
        }

        if (TYPE_MAP.containsKey(clazz)) {
            return TYPE_MAP[clazz]!!
        }

        return PropertyType.OBJECT
    }

    fun of(clazz: Class<*>): PropertyType {
        return of(GenericType.of(clazz))
    }
}
