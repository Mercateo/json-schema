package com.mercateo.jsonschema.property

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

import com.mercateo.jsonschema.property.MethodCollectorClasses.StaticMethod

class MethodCollectorTest {
    @Test
    fun collectsMethods() {
        val methodCollector = MethodCollector()
        val rawProperties = methodCollector.forType(TestClass::class.java).toList()

        assertThat(rawProperties).extracting("name").containsExactlyInAnyOrder("value", "number", "enabled", "running")
    }

    @Test
    fun ignoresStaticMethods() {
        val methodCollector = MethodCollector()
        val rawProperties = methodCollector.forType(StaticMethod::class.java).toList()

        assertThat(rawProperties).extracting("name").containsExactly("string")
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

        fun setRunning() {

        }

        fun running(): Boolean {
            return false
        }
    }
}
