package com.mercateo.jsonschema.generictype

import com.googlecode.gentyref.GenericTypeReflector

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class GenericParameterizedType<T>(type: ParameterizedType, rawType: Class<T>) : GenericTypeAbstract<T, ParameterizedType>(rawType, type) {

    override val simpleName: String
        get() = type.typeName

    override val name: String
        get() = type.typeName

    override val containedType: GenericType<*>
        get() {
            val actualTypeArguments = type.actualTypeArguments
            if (actualTypeArguments.size > 1) {
                throw IllegalStateException(type.toString() + " not supported for subtyping")
            }
            return GenericType.of(actualTypeArguments[0], rawType)
        }

    override fun toString(): String {
        return type.toString()
    }

}
