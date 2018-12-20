package com.mercateo.jsonschema.generictype


class GenericTypeHierarchy {
    fun hierarchy(genericType: GenericType<*>): Sequence<GenericType<*>> {

        return sequence {
            var currentType: GenericType<*>? = genericType

            while (currentType != null) {
                yield(currentType as GenericType<*>)
                currentType = currentType.superType
            }
        }
    }
}
