package com.mercateo.jsonschema.generictype

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.util.*
import java.util.Objects.requireNonNull

internal abstract class GenericTypeAbstract<T, out U : Type>(
        override val rawType: Class<T>,
        override val type: U
) : GenericType<T> {

    override fun isInstanceOf(clazz: Class<*>): Boolean {
        return requireNonNull(clazz).isAssignableFrom(rawType)
    }

    override val isIterable: Boolean
        get() = Iterable::class.java.isAssignableFrom(rawType)

    override val declaredFields: Array<Field>
        get() = rawType.declaredFields

    override val declaredMethods: Array<Method>
        get() = rawType.declaredMethods

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || !GenericType::class.java.isAssignableFrom(o.javaClass)) {
            return false
        }
        if (javaClass != o.javaClass) return false
        val that = o as GenericType<*>?
        return rawType == that!!.rawType && type == that.type
    }

    override fun hashCode(): Int {
        return Objects.hash(type, type)
    }
}
