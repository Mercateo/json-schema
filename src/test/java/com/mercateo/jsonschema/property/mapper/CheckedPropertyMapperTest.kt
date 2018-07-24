package com.mercateo.jsonschema.property.mapper

import com.mercateo.jsonschema.SchemaContext
import com.mercateo.jsonschema.mapper.PropertyChecker
import com.mercateo.jsonschema.property.BasicPropertyBuilder
import com.mercateo.jsonschema.property.collector.FieldCollector
import com.mercateo.jsonschema.property.from
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyAll
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test


class CheckedPropertyMapperTest {

    lateinit var uut: CheckedPropertyMapper

    lateinit var propertyChecker: PropertyChecker

    @Before
    fun setUp() {
        uut = CheckedPropertyMapper()
        propertyChecker = mockk()
    }

    @Test
    fun mapsProperties() {
        val propertyBuilder = BasicPropertyBuilder(emptyMap(), FieldCollector())

        val property = propertyBuilder.from(CheckedPropertyMapperClasses.Simple::class.java)

        every { propertyChecker.test(any()) } returns true

        val result = uut.from(property, SchemaContext(propertyChecker))

        assertThat(result.children).hasSize(2)

        verifyAll {
            propertyChecker.test(property.children.find { it.name == "foo" }!!)
            propertyChecker.test(property.children.find { it.name == "bar" }!!)
        }
    }

    @Test
    fun disablesProperties() {
        val propertyBuilder = BasicPropertyBuilder(emptyMap(), FieldCollector())

        val property = propertyBuilder.from(CheckedPropertyMapperClasses.Simple::class.java)

        every { propertyChecker.test(any()) } returns false

        val result = uut.from(property, SchemaContext(propertyChecker))

        assertThat(result.children).isEmpty()
    }

    @Test
    fun handlesNestedProperties() {
        val propertyBuilder = BasicPropertyBuilder(emptyMap(), FieldCollector())
        val referencedPropertyMapper = ReferencedPropertyMapper()

        val property = referencedPropertyMapper.from(
                propertyBuilder.from(CheckedPropertyMapperClasses.Nested::class.java), SchemaContext(propertyChecker))

        every { propertyChecker.test(any()) } returns true

        val result = uut.from(property, SchemaContext(propertyChecker))

        assertThat(result.children).hasSize(2)

        val propertyWithChildren = property.children.find { it.reference == null }!!

        verifyAll {
            propertyChecker.test(property.children.find { it.name == "first" }!!)
            propertyChecker.test(property.children.find { it.name == "second" }!!)
            propertyChecker.test(propertyWithChildren.children.find { it.name == "foo" }!!)
            propertyChecker.test(propertyWithChildren.children.find { it.name == "bar" }!!)
        }
    }
}