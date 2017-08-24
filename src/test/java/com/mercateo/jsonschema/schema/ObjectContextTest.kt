package com.mercateo.jsonschema.schema

import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.property.PropertyDescriptor
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

data class PropertyA(
        var count: Int? = null,
        var inner: PropertyB? = null)


data class PropertyB(
        var value: String? = null
)

class ObjectContextTest {

    lateinit var outer: Property<Nothing, PropertyA>
    lateinit var inner : Property<PropertyA, PropertyB>

    @Before
    fun setup() {
        outer = Property("#", mock<PropertyDescriptor<PropertyA>> {}, { x: Nothing -> null}, emptyMap(), Property.Context.Unconnected )
        inner = Property("inner", mock<PropertyDescriptor<PropertyB>> {}, {x: PropertyA -> x.inner}, emptyMap(), Property.Context.Unconnected )
    }

    @Test
    fun shouldCreateInnerWithDefaultValue() {
        val defaultValue = createProperty("foo", 5)

        val propertyAContext = ObjectContext(outer, defaultValue = defaultValue)

        val propertyBContext = propertyAContext.createInner(inner, PropertyA::inner)

        assertThat(propertyBContext.defaultValue).isEqualTo(defaultValue.inner)
    }

    @Test
    fun shouldCreateInnerWithoutDefaultValue() {
        val propertyAContext = ObjectContext(outer)

        val propertyBContext = propertyAContext.createInner(inner,
                PropertyA::inner)

        assertThat(propertyBContext.defaultValue).isNull();
    }

    @Test
    fun shouldCreateInnerWithNullResultForDefaultValue() {
        val defaultValue = PropertyA()

        val propertyAContext = ObjectContext(outer, defaultValue = defaultValue)

        val propertyBContext = propertyAContext.createInner(inner,
                { propA -> propA.inner })

        assertThat(propertyBContext.defaultValue).isNull()
    }

    @Test
    fun shouldCreateInnerWithAllowedValues() {
        val propertyA1 = createProperty("bar", 6)
        val propertyA2 = createProperty("baz", 7)

        val propertyAContext = ObjectContext(outer, allowedValues = setOf(propertyA1, propertyA2))

        val propertyBContext = propertyAContext.createInner(inner,
                { propA -> propA.inner })

        assertThat(propertyBContext.allowedValues).containsExactlyInAnyOrder(propertyA1.inner,
                propertyA2.inner)
    }

    private fun createProperty(value: String, count: Int): PropertyA {
        return PropertyA(count = count, inner = PropertyB(value))
    }
}