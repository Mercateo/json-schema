package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.property.UnwrappedPropertyMapperClasses.*
import com.mercateo.jsonschema.property.collector.FieldCollector
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.IterableAssert
import org.junit.Before
import org.junit.Test

class UnwrappedPropertyMapperTest {

    private lateinit var propertyBuilder: PropertyBuilder

    @Before
    fun setUp() {
        propertyBuilder = PropertyBuilderWrapper(
                BasicPropertyBuilder(emptyMap(), FieldCollector()),
                UnwrappedPropertyMapper(Unwrap::class.java)
        )
    }

    @Test
    fun singleLevelUnwrap() {
        val unwrappedProperty = propertyBuilder.from(PropertyHolder::class.java)

        assertThat(unwrappedProperty.children).extracting("name").containsExactly("bar", "foo", "qux")
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


}
