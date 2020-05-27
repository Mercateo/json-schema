package com.mercateo.jsonschema.generictype

import com.googlecode.gentyref.GenericTypeReflector
import java.lang.reflect.GenericArrayType

internal class GenericArray<T>(
    arrayType: GenericArrayType,
    rawType: Class<T>
) : GenericTypeAbstract<T, GenericArrayType>(rawType, arrayType) {

    override val simpleName: String
        get() = type.typeName

    override val name: String
        get() = type.typeName

    override val containedType: GenericType<Any>
        get() = GenericType.of(GenericTypeReflector.getArrayComponentType(type), rawType.componentType)

    override val superType: GenericType<Any>?
        get() = null

    override val isIterable: Boolean
        get() = true
}
