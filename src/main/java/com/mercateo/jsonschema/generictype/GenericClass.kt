package com.mercateo.jsonschema.generictype

import com.googlecode.gentyref.GenericTypeReflector
import java.lang.reflect.Type

internal class GenericClass<T>(type: Class<T>) : GenericTypeAbstract<T, Class<*>>(type, type) {

    override val containedType: GenericType<*>
        get() {
            if (rawType.isArray) {
                return GenericType.of(GenericTypeReflector.getArrayComponentType(rawType),
                        rawType.componentType)
            }
            throw IllegalAccessError("GenericClass $simpleName has no contained type")
        }

    override val superType: GenericType<T>?
        get() {
            val superclass: Class<T>? = rawType.superclass as Class<T>?
            return if (superclass != null) {
                val exactSuperType: Type = GenericTypeReflector.getExactSuperType(
                        rawType, superclass)
                GenericType.of(exactSuperType, superclass)
            } else {
                null
            }
        }

    override val isIterable: Boolean
        get() = rawType.isArray

    override fun toString(): String {
        return rawType.toString()
    }
}
