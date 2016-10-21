package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.property.UnwrappedPropertyBuilderClasses.*
import org.junit.Before
import org.junit.Test

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.IterableAssert

class UnwrappedPropertyBuilderTest {

    private var unwrappedPropertyBuilder: UnwrappedPropertyBuilder? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val propertyBuilder = PropertyBuilderDefault(listOf(FieldCollector(FieldCollectorConfig())))
        unwrappedPropertyBuilder = UnwrappedPropertyBuilder(propertyBuilder, Unwrap::class.java)
    }

    @Test
    @Throws(Exception::class)
    fun singleLevelUnwrap() {
        val unwrappedProperty = unwrappedPropertyBuilder!!.from(PropertyHolder::class.java)

        assertThat(unwrappedProperty.children).extracting("name").containsExactlyInAnyOrder("foo", "bar")
    }

    @Test
    @Throws(Exception::class)
    fun twoLevelUnwrap() {
        val unwrappedProperty = unwrappedPropertyBuilder!!.from(SecondLevelPropertyHolder::class.java)

        assertThat(unwrappedProperty.children).extracting("name").containsExactlyInAnyOrder("foo", "bar")
    }

    fun <T> assertThat(actual: Iterable<T>) = IterableAssert(actual)

    @Test
    @Throws(Exception::class)
    fun singleLevelUnwrapGetValue() {
        val unwrappedProperty = unwrappedPropertyBuilder!!.from(PropertyHolder::class.java)

        val propertyHolder = PropertyHolder()
        propertyHolder.unwrappedPropertyHolder = UnwrappedPropertyHolder()
        propertyHolder.unwrappedPropertyHolder!!.foo = "value1"

        val firstElement = unwrappedProperty.children.first()

        assertThat(firstElement.getValue(propertyHolder)).isEqualTo("value1")
    }


}
