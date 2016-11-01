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

    override val superType: GenericType<in T>?
        get() {
            val superclass = rawType.superclass as Class<T>?
            return if (superclass != null) {
                val exactSuperType: Type = GenericTypeReflector.getExactSuperType(type,
                        superclass)
                GenericType.of(exactSuperType, superclass)
            } else
                null
        }

    override fun toString(): String {
        return type.toString()
    }

}
