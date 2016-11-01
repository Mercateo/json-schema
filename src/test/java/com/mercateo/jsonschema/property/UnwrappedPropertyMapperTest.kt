package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.property.UnwrappedPropertyMapperClasses.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.IterableAssert
import org.junit.Before
import org.junit.Test

class UnwrappedPropertyMapperTest {

    private lateinit var propertyBuilder: PropertyBuilder

    @Before
    @Throws(Exception::class)
    fun setUp() {
        propertyBuilder = PropertyBuilderWrapper(
                PropertyBuilderDefault(FieldCollector(FieldCollectorConfig())),
                UnwrappedPropertyMapper(Unwrap::class.java)
        )
    }

    @Test
    @Throws(Exception::class)
    fun singleLevelUnwrap() {
        val unwrappedProperty = propertyBuilder.from(PropertyHolder::class.java)

        assertThat(unwrappedProperty.children).extracting("name").containsExactlyInAnyOrder("foo", "bar")
    }

    @Test
    @Throws(Exception::class)
    fun twoLevelUnwrap() {
        val unwrappedProperty = propertyBuilder.from(SecondLevelPropertyHolder::class.java)

        assertThat(unwrappedProperty.children).extracting("name").containsExactlyInAnyOrder("foo", "bar")
    }

    fun <T> assertThat(actual: Iterable<T>) = IterableAssert(actual)

    @Test
    @Throws(Exception::class)
    fun singleLevelUnwrapGetValue() {
        val unwrappedProperty = propertyBuilder.from(PropertyHolder::class.java)

        val propertyHolder = PropertyHolder()
        propertyHolder.unwrappedPropertyHolder = UnwrappedPropertyHolder()
        propertyHolder.unwrappedPropertyHolder!!.foo = "value1"

        val firstElement = unwrappedProperty.children.first()

        assertThat(firstElement.getValue(propertyHolder)).isEqualTo("value1")
    }


}
