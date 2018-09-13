package com.mercateo.jsonschema.property.mapper

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.mercateo.jsonschema.property.BasicPropertyBuilder
import com.mercateo.jsonschema.property.PropertyBuilderWrapper
import com.mercateo.jsonschema.property.mapper.UnwrappedPropertyMapperClasses.*
import com.mercateo.jsonschema.property.collector.FieldCollector
import com.mercateo.jsonschema.property.from
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.IterableAssert
import org.junit.Before
import org.junit.Test

class UnwrappedPropertyMapperTest {

    private lateinit var propertyBuilder: PropertyBuilderWrapper

    @Before
    fun setUp() {
        propertyBuilder = PropertyBuilderWrapper(
                BasicPropertyBuilder(rawPropertyCollectors = listOf(FieldCollector())),
                UnwrappedPropertyMapper(object : UnwrappedPropertyUpdater<JsonUnwrapped>(JsonUnwrapped::class.java) {
                    override fun updateName(name: String, annotation: JsonUnwrapped): String {
                        return annotation.prefix + name + annotation.suffix
                    }
                })
        )
    }

    @Test
    fun singleLevelUnwrap() {
        val unwrappedProperty = propertyBuilder.from(PropertyHolder::class.java)

        assertThat(unwrappedProperty.children).extracting("name").containsExactly("bar", "foo", "qux")
    }

    @Test
    fun cachesResult() {
        val firstUnwrappedProperty = propertyBuilder.from(PropertyHolder::class.java)
        val secondUnwrappedProperty = propertyBuilder.from(PropertyHolder::class.java)

        assertThat(firstUnwrappedProperty).isSameAs(secondUnwrappedProperty)
    }

    @Test
    fun cachesIntermediateProperties() {
        val firstUnwrappedPropertyHolder = propertyBuilder.from(WrappedPropertyHolder1::class.java)
        val secondUnwrappedPropertyHolder = propertyBuilder.from(WrappedPropertyHolder2::class.java)

        val firstUnwrappedProperty = firstUnwrappedPropertyHolder.children.first()
        val secondUnwrappedProperty = firstUnwrappedPropertyHolder.children.first()

        assertThat(firstUnwrappedProperty).isSameAs(secondUnwrappedProperty)
    }

    @Test
    fun twoLevelUnwrap() {
        val unwrappedProperty = propertyBuilder.from(SecondLevelPropertyHolder::class.java)

        assertThat(unwrappedProperty.children).extracting("name").containsExactly("bar", "foo", "quux", "qux")
    }

    fun <T> assertThat(actual: Iterable<T>) = IterableAssert(actual)

    @Test
    fun singleLevelUnwrapGetValue() {
        val unwrappedProperty = propertyBuilder.from(PropertyHolder::class.java)

        val propertyHolder = PropertyHolder()
        propertyHolder.unwrappedPropertyHolder = UnwrappedPropertyHolder()
        propertyHolder.unwrappedPropertyHolder!!.foo = "value1"

        val firstElement = unwrappedProperty.children.first { it.name == "foo" }

        assertThat(firstElement.getValue(propertyHolder)).isEqualTo("value1")
    }

    @Test
    fun doubleSingleLevelUnwrapGetValue() {

        val unwrappedProperty = propertyBuilder.from(DoubleUnwrappedPropertyHolder::class.java)

        val propertyHolder = DoubleUnwrappedPropertyHolder()
        propertyHolder.unwrappedPropertyHolder1 = UnwrappedPropertyHolder()
        propertyHolder.unwrappedPropertyHolder1!!.foo = "value1"
        propertyHolder.unwrappedPropertyHolder2 = UnwrappedPropertyHolder()
        propertyHolder.unwrappedPropertyHolder2!!.bar = "value2"

        val firstElement = unwrappedProperty.children.first { it.name == "bazfoo" }
        assertThat(firstElement.getValue(propertyHolder)).isEqualTo("value1")

        val secondElement = unwrappedProperty.children.first { it.name == "barqux" }
        assertThat(secondElement.getValue(propertyHolder)).isEqualTo("value2")
    }

}
