package com.mercateo.jsonschema.generictype

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class GenericArrayTest {

    internal class TestClass<T> {
        private val values: Array<T>? = null
        private val typedValues: Array<String>? = null
    }

    private lateinit var genericType: GenericType<*>

    private lateinit var concreteType: GenericType<Any>

    @Before
    @Throws(NoSuchFieldException::class)
    fun setUp() {
        val genericField = TestClass::class.java.getDeclaredField("values")
        this.genericType = GenericType.of(genericField.genericType, genericField.type)

        val concreteField = TestClass::class.java.getDeclaredField("typedValues")
        this.concreteType = GenericType.of(concreteField.genericType, concreteField.type)
    }

    @Test
    fun getSimpleNameReturnsCorrectName() {
        assertThat(genericType.simpleName).isEqualTo("T[]")
    }

    @Test
    fun getNameReturnsCorrectName() {
        assertThat(genericType.name).isEqualTo("T[]")
    }

    @Test
    fun getSupertypeAlwaysReturnsNull() {
        assertThat(genericType.superType).isNull()
    }

    @Test
    fun deliversContainedType() {
        assertThat(concreteType.containedType).isEqualTo(GenericType.of(String::class.java))
    }

    @Test
    fun shouldBeIterable() {
        assertThat(concreteType.isIterable).isTrue()
    }

    @Test
    fun shouldNotBeEnum() {
        assertThat(concreteType.isEnum).isFalse()
    }
}
