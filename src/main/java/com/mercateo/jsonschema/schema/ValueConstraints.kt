package com.mercateo.jsonschema.schema

class ValueConstraints {

    var max: Long? = null
        private set

    var min: Long? = null
        private set

    constructor(max: Long?, min: Long?) {
        if (max != null && min != null && min > max) {
            throw IllegalArgumentException(
                    "Minimum value $min is larger than maximum value $max")
        }
        this.max = max
        this.min = min
    }

    private constructor() : this(null, null)

    companion object {
        private val EMPTY_CONSTRAINTS = ValueConstraints()

        fun empty(): ValueConstraints {
            return EMPTY_CONSTRAINTS
        }
    }
}
