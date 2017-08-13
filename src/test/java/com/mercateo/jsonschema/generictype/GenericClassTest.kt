package com.mercateo.jsonschema.generictype

import com.googlecode.gentyref.GenericTypeReflector
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GenericClassTest {

    @Test
    @Throws(NoSuchFieldException::class)
    fun testGetNameForTypedArray() {
        val field = TestClass::class.java.getDeclaredField("floatArray")
        val type = GenericTypeReflector.getExactFieldType(field, TestClass::class.java)

        @SuppressWarnings("rawtypes")
        val genericType = GenericClass(type as Class<*>)

        Assertions.assertThat(genericType.name).isEqualTo("[Ljava.lang.Float;")
        Assertions.assertThat(genericType.simpleName).isEqualTo("Float[]")
    }

    @Test
    @Throws(NoSuchFieldException::class)
    fun testGetNameForPrimitiveArray() {
        val field = TestClass::class.java.getDeclaredField("primitiveFloatArray")
        val type = GenericTypeReflector.getExactFieldType(field, TestClass::class.java)

        @SuppressWarnings("rawtypes")
        val genericType = GenericClass(type as Class<*>)

        Assertions.assertThat(genericType.name).isEqualTo("[F")
        Assertions.assertThat(genericType.simpleName).isEqualTo("float[]")
    }

    @Test
    fun equalClassesShouldBeEqual() {
        val genericType1 = GenericType.of(String::class.java)
        val genericType2 = GenericType.of(String::class.java)

        assertThat(genericType1).isEqualTo(genericType2)
    }

    @Test
    fun nonGenericTypeInstanceShouldNotBeEqual() {
        val genericType1 = GenericType.of(String::class.java)
        val genericType2 = String::class.java

        assertThat(genericType1).isNotEqualTo(genericType2)
    }

    @Test
    fun isInstanceShouldWorkForPlainClasses() {
        val genericType1 = GenericType.of(String::class.java)

        assertThat(genericType1.isInstanceOf(String::class.java)).isTrue()
    }

    @Test
    fun shouldHaveClassStringRepresentation() {
        val genericType1 = GenericType.of(String::class.java)

        assertThat(genericType1.toString()).isEqualTo(String::class.java.toString())
    }
}