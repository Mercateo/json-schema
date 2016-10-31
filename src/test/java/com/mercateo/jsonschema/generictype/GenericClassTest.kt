package com.mercateo.jsonschema.generictype

import com.googlecode.gentyref.GenericTypeReflector
import org.assertj.core.api.Assertions
import org.junit.Test
import org.assertj.core.api.KotlinAssertions.assertThat
import java.lang.reflect.ParameterizedType

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
    fun name() {
        val genericType1 = GenericType.of(String::class.java)
        val genericType2 = GenericType.of(String::class.java)

        assertThat(genericType1).isEqualTo(genericType2)
    }
}