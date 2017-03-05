package com.mercateo.jsonschema.property


import com.mercateo.jsonschema.generictype.GenericType

interface RawPropertyCollector {
    fun <S> forType(genericType: GenericType<S>): Sequence<RawProperty<S, *>>

    fun <S> forType(clazz: Class<S>): Sequence<RawProperty<S, *>> {
        return forType(GenericType.of(clazz))
    }
}
