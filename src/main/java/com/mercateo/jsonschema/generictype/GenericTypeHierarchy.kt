package com.mercateo.jsonschema.generictype

import kotlin.coroutines.experimental.buildSequence

class GenericTypeHierarchy {
    fun hierarchy(genericType: GenericType<*>): Sequence<GenericType<*>> {

        return buildSequence {
            var currentType: GenericType<*>? = genericType

            while (currentType != null) {
                yield(currentType as GenericType<*>)
                currentType = currentType.superType
            }
        }
    }
}
