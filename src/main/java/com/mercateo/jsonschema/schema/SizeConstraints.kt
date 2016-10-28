package com.mercateo.jsonschema.schema

import javax.validation.constraints.Size

class SizeConstraints {

    var max: Int? = null
        private set

    var min: Int? = null
        private set

    @Suppress("unused")
    constructor(size: Size) : this(size.max, size.min) {
    }

    @Suppress("unused")
    private constructor(max: Int?, min: Int?) {
        if (min != null && max != null && min > max) {
            throw IllegalArgumentException(
                    "Minimum value $min is larger than maximum value $max")
        }
        if (min != null && min < 0) {
            throw IllegalArgumentException("Supplied arguments must be non-negative")
        }
        this.max = if (max == Integer.MAX_VALUE) null else max
        this.min = if (min == 0) null else min
    }

    private constructor() : this(null, null)

    companion object {
        private val EMPTY_CONSTRAINTS = SizeConstraints()

        fun empty(): SizeConstraints {
            return EMPTY_CONSTRAINTS
        }
    }
}
