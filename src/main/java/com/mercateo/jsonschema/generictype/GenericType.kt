package com.mercateo.jsonschema.generictype

import com.googlecode.gentyref.GenericTypeReflector

import java.lang.reflect.*

import java.util.Objects.requireNonNull

interface GenericType<out T> {
    val rawType: Class<out T>

    val simpleName: String

    val name: String

    val type: Type

    val containedType: GenericType<*>

    fun isInstanceOf(clazz: Class<*>): Boolean

    val isIterable: Boolean

    val declaredFields: Array<Field>

    val declaredMethods: Array<Method>

    val superType: GenericType<Any>?

    companion object {

        fun of(type: Type): GenericType<Any> {
            return of<Any>(type, null)
        }

        fun <T> of(type: Class<T>): GenericType<T> {
            return of(type, null)
        }

        @SuppressWarnings("unchecked")
        fun <T> of(type: Type, rawType: Class<T>?): GenericType<T> {
            if (type is ParameterizedType) {
                return GenericParameterizedType(type, type.rawType as Class<T>)
            } else if (type is Class<*>) {
                return GenericClass(type as Class<T>)
            } else if (type is GenericArrayType) {
                return GenericArray(type, requireNonNull<Class<T>>(rawType))
            }
            run { throw IllegalStateException("unhandled type " + type) }
        }

        fun ofField(field: Field, type: Type): GenericType<Any> {
            val fieldClass = field.type
            val fieldType = GenericTypeReflector.getExactFieldType(field, type)
            return of(fieldType, fieldClass)
        }

        fun ofMethod(method: Method, type: Type): GenericType<Any> {
            val returnClass = method.returnType
            val returnType = GenericTypeReflector.getExactReturnType(method, type)
            return of(returnType, returnClass)
        }
    }

}
