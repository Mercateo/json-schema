package com.mercateo.jsonschema.generictype

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.util.*

class GenericArrayTest {

    internal class TestClass<T> {
        private val values: Array<Optional<String>>? = null
    }

    private lateinit var genericType: GenericType<*>

    @Before
    @Throws(NoSuchFieldException::class)
    fun setUp() {
        val genericField = TestClass::class.java.getDeclaredField("values")
        this.genericType = GenericType.of(genericField.genericType, genericField.type)
    }

    @Test
    fun getSimpleNameReturnsCorrectName() {
        assertThat(genericType.simpleName).isEqualTo("java.util.Optional<java.lang.String>[]")
    }

    @Test
    fun getNameReturnsCorrectName() {
        assertThat(genericType.name).isEqualTo("java.util.Optional<java.lang.String>[]")
    }

    @Test
    fun getSupertypeAlwaysReturnsNull() {
        assertThat(genericType.superType).isNull()
    }

    @Test
    fun deliversContainedType() {
        assertThat(genericType.containedType.rawType.name).isEqualTo("java.util.Optional")
    }

    @Test
    fun shouldBeIterable() {
        assertThat(genericType.isIterable).isTrue
    }

    @Test
    fun shouldNotBeEnum() {
        assertThat(genericType.isEnum).isFalse
    }
}
