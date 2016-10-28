package com.mercateo.jsonschema.generictype

class GenericTypeHierarchy {
    fun hierarchy(genericType: GenericType<*>): Sequence<GenericType<*>> {
        return GenericTypeIterator(genericType).asSequence()
    }

    private class GenericTypeIterator internal constructor(internal var currentType: GenericType<*>?) : Iterator<GenericType<*>> {

        override fun hasNext(): Boolean {
            return currentType != null
        }

        override fun next(): GenericType<*> {
            val type = currentType
            currentType = currentType!!.superType
            return type!!
        }
    }
}
