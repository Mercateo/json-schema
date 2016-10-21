package com.mercateo.jsonschema.property


import com.mercateo.jsonschema.generictype.GenericType

interface RawPropertyCollector {
    fun forType(genericType: GenericType<*>): Sequence<RawProperty>

    fun forType(clazz: Class<*>): Sequence<RawProperty> {
        return forType(GenericType.of(clazz))
    }
}
