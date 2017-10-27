package com.mercateo.jsonschema.property.collector

import com.mercateo.jsonschema.property.collector.MethodCollectorClasses.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

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

    @Test
    fun inheritsAnnotationFromSuperclassMethod() {
        val methodCollector = MethodCollector()
        val rawProperties = methodCollector.forType(SubClass::class.java).toList()

        assertThat(rawProperties).extracting("name").containsExactlyInAnyOrder("foo", "bar")
        assertThat(rawProperties.first { it.name == "foo" }.annotations).containsKey(MethodCollectorClasses.MethodAnnotation::class.java)
    }

    @Test
    fun inheritsAnnotationFromInterfaceMethod() {
        val methodCollector = MethodCollector()
        val rawProperties = methodCollector.forType(ImplementingClass::class.java).toList()

        assertThat(rawProperties).extracting("name").containsExactlyInAnyOrder("foo", "bar")
        assertThat(rawProperties.first { it.name == "foo" }.annotations).containsKeys(MethodCollectorClasses.MethodAnnotation::class.java, MethodCollectorClasses.OtherMethodAnnotation::class.java)
    }

    @Test
    fun ignoresOverriddenObjectMethods() {
        val methodCollector = MethodCollector()
        val rawProperties = methodCollector.forType(OverridingHashCode::class.java).toList()

        assertThat(rawProperties).isEmpty()
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
