package com.mercateo.jsonschema.generictype

import org.junit.Before
import org.junit.Test

import org.assertj.core.api.Assertions.assertThat

class GenericArrayTest {

    internal class TestClass<T> {
        private val values: Array<T>? = null
    }

    private lateinit var genericType: GenericType<*>

    @Before
    @Throws(NoSuchFieldException::class)
    fun setUp() {
        val field = TestClass::class.java.getDeclaredField("values")
        this.genericType = GenericType.of(field.genericType, field.type)
    }

    @Test
    fun getSimpleNameReturnsCorrectName() {
        assertThat(genericType.simpleName).isEqualTo("T[]")
    }

    @Test
    fun getSupertypeAlwaysReturnsNull() {
        assertThat(genericType.superType).isNull()
    }

}
