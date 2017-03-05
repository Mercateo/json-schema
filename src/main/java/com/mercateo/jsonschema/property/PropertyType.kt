package com.mercateo.jsonschema.property

import java.util.*

enum class PropertyType {
    OBJECT, STRING, BOOLEAN, INTEGER, NUMBER, ARRAY;

    companion object {

        internal val PRIMITIVE_TYPES: Set<PropertyType> = HashSet(Arrays.asList(STRING,
                BOOLEAN, INTEGER, NUMBER))
    }
}
