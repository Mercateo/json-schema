package com.mercateo.jsonschema.property

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MethodCollectorTest {
    @Test
    fun collectMethods() {
        val methodCollector = MethodCollector()
        val rawProperties = methodCollector.forType(TestClass::class.java).toList()

        assertThat(rawProperties).extracting("name").containsExactlyInAnyOrder("value", "number", "enabled", "running")
    }

    internal class TestClass {

        var value: Float?
            get() = null
            set(value) {

            }

        fun addNumber(number: Double?) {

        }

        fun number(): Double? {
            return null
        }

        var isEnabled: Boolean?
            get() = null
            set(enabled) {

            }

        fun setRuning() {

        }

        fun running(): Boolean {
            return false
        }
    }
}
