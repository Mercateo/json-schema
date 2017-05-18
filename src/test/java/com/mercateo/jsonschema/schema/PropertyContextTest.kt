package com.mercateo.jsonschema.schema

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

data class PropertyA(
        var count: Int? = null,
        var inner: PropertyB? = null)


data class PropertyB(
        var value: String? = null
)

class PropertyContextTest {

    @Test
    fun shouldCreateInnerWithDefaultValue() {
        val propertyA = createProperty("foo", 5)

        val propertyAContext = PropertyContext(defaultValue = propertyA)

        val propertyBContext = propertyAContext.createInner(PropertyA::inner)

        assertThat(propertyBContext.defaultValue).isEqualTo(propertyA.inner)
    }

    @Test
    fun shouldCreateInnerWithoutDefaultValue() {
        val propertyAContext = PropertyContext<PropertyA>()

        val propertyBContext = propertyAContext.createInner(
                PropertyA::inner)

        assertThat(propertyBContext.defaultValue).isNull();
    }

    @Test
    fun shouldCreateInnerWithNullResultForDefaultValue() {
        val propertyA = PropertyA()

        val propertyAContext = PropertyContext(defaultValue = propertyA)

        val propertyBContext = propertyAContext.createInner(
                { propA -> propA.inner })

        assertThat(propertyBContext.defaultValue).isNull()
    }

    @Test
    fun shouldCreateInnerWithAllowedValues() {
        val propertyA1 = createProperty("bar", 6)
        val propertyA2 = createProperty("baz", 7)

        val propertyAContext = PropertyContext(allowedValues = setOf(propertyA1, propertyA2))

        val propertyBContext = propertyAContext.createInner(
                { propA -> propA.inner })

        assertThat(propertyBContext.allowedValues).containsExactlyInAnyOrder(propertyA1.inner,
                propertyA2.inner)
    }

    private fun createProperty(value: String, count: Int): PropertyA {
        return PropertyA(count = count, inner = PropertyB(value))
    }
}