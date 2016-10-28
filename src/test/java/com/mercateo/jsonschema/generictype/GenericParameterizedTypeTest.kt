package com.mercateo.jsonschema.generictype

import com.googlecode.gentyref.GenericTypeReflector
import org.junit.Test

import java.lang.reflect.ParameterizedType

import org.assertj.core.api.Assertions.assertThat

class GenericParameterizedTypeTest {

    @Test
    @Throws(NoSuchFieldException::class)
    fun testGetContainedType() {
        val field = TestClass::class.java.getDeclaredField("doubleList")
        val type = GenericTypeReflector.getExactFieldType(field, TestClass::class.java)

        @SuppressWarnings("rawtypes")
        val genericType = GenericParameterizedType(
                type as ParameterizedType, List::class.java)

        val containedType1 = genericType.containedType
        assertThat(containedType1.rawType).isEqualTo(java.util.List::class.java)

        val containedType2 = containedType1.containedType
        assertThat(containedType2.rawType).isEqualTo(java.lang.Double::class.java)
    }

    @Test
    @Throws(NoSuchFieldException::class)
    fun testGetContainedTypeWithGenericTypeParameter() {
        val superclass = TestClass::class.java.superclass
        val field = superclass.getDeclaredField("object")
        val type = GenericTypeReflector.getExactFieldType(field, TestClass::class.java)

        val genericType = GenericType.of(type)

        assertThat(genericType.rawType).isEqualTo(java.lang.Boolean::class.java)
    }

}
