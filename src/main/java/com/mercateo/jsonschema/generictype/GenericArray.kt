package com.mercateo.jsonschema.generictype

import java.lang.reflect.GenericArrayType

import com.googlecode.gentyref.GenericTypeReflector

internal class GenericArray<T>(
        arrayType: GenericArrayType,
        rawType: Class<T>
) : GenericTypeAbstract<T, GenericArrayType>(rawType, arrayType) {

    override val simpleName: String
        get() = type.typeName

    override val containedType: GenericType<*>
        get() = GenericType.of(GenericTypeReflector.getArrayComponentType(type), rawType.componentType)

    override val superType: GenericType<in T>?
        get() = null

    override val isIterable: Boolean
        get() = true
}
