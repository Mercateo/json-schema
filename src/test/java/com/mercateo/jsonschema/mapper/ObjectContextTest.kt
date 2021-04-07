package com.mercateo.jsonschema.mapper

import com.mercateo.jsonschema.generictype.GenericType
import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.property.PropertyDescriptor
import com.mercateo.jsonschema.property.PropertyType
import io.mockk.every
import io.mockk.mockk
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

    private lateinit var outer: Property<Nothing, PropertyA>
    private lateinit var inner: Property<PropertyA, PropertyB>

    @Before
    fun setup() {
        val propertyDescriptorA = mockk<PropertyDescriptor<PropertyA>> {}
        every { propertyDescriptorA.children } returns emptyList()
        every { propertyDescriptorA.genericType } returns GenericType.of(PropertyA::class.java)
        every { propertyDescriptorA.propertyType } returns PropertyType.OBJECT
        outer = Property("#", propertyDescriptorA, { null }, emptyMap(), Property.Context.Unconnected)

        val propertyDescriptorB = mockk<PropertyDescriptor<PropertyB>> {}
        every { propertyDescriptorB.children } returns emptyList()
        every { propertyDescriptorB.genericType } returns GenericType.of(PropertyB::class.java)
        every { propertyDescriptorB.propertyType } returns PropertyType.OBJECT
        inner = Property("inner", propertyDescriptorB, { x: PropertyA -> x.inner }, emptyMap(), Property.Context.Unconnected)
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

        assertThat(propertyBContext.defaultValue).isNull()
    }

    @Test
    fun shouldCreateInnerWithNullResultForDefaultValue() {
        val defaultValue = PropertyA()

        val propertyAContext = ObjectContext(outer, defaultValue = defaultValue)

        val propertyBContext = propertyAContext.createInner(inner
        ) { propA -> propA.inner }

        assertThat(propertyBContext.defaultValue).isNull()
    }

    @Test
    fun shouldCreateInnerWithAllowedValues() {
        val propertyA1 = createProperty("bar", 6)
        val propertyA2 = createProperty("baz", 7)

        val propertyAContext = ObjectContext(outer, allowedValues = setOf(propertyA1, propertyA2))

        val propertyBContext = propertyAContext.createInner(inner
        ) { propA -> propA.inner }

        assertThat(propertyBContext.allowedValues).containsExactlyInAnyOrder(propertyA1.inner,
                propertyA2.inner)
    }

    private fun createProperty(value: String, count: Int): PropertyA {
        return PropertyA(count = count, inner = PropertyB(value))
    }
}