package com.mercateo.jsonschema.mapper

import com.mercateo.jsonschema.property.Property

import java.util.function.Predicate

interface PropertyChecker : Predicate<Property<*, *>> {

    companion object {
        fun fromPredicate(predicate: Predicate<Property<*, *>>): PropertyChecker
                = object : PropertyChecker {
            override fun test(t: Property<*, *>): Boolean {
                return predicate.test(t)
            }
        }
    }
}
