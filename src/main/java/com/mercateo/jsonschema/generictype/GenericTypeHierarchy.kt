package com.mercateo.jsonschema.generictype


class GenericTypeHierarchy {
    fun hierarchy(genericType: GenericType<*>): Sequence<GenericType<*>> {

        return sequence {
            var currentType: GenericType<*>? = genericType

            while (currentType != null) {
                @Suppress("USELESS_CAST")
                yield(currentType as GenericType<*>)
                currentType = currentType.superType
            }
        }
    }
}
