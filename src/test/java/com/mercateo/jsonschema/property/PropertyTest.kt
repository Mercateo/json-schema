package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType
import com.mercateo.jsonschema.property.collector.FieldCollector
import com.mercateo.jsonschema.property.mapper.PropertyClasses
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test


class PropertyTest {

    lateinit var propertyBuilder: BasicPropertyBuilder

    @Before
    fun setUp() {
        propertyBuilder = BasicPropertyBuilder(rawPropertyCollectors = listOf(FieldCollector()))
    }

    @Test
    fun flatUpdateVisitsItselfOnly() {
        val property = propertyBuilder.from(GenericType.of(PropertyClasses.Foo::class.java))

        val visited = mutableListOf<Property<*, *>>()

        property.update(Property.Updater.Flat({ visited.add(it); it }))

        assertThat(visited).extracting("name").containsExactly("#")
    }

    @Test
    fun flatUpdateChildrenVisitsDirectChildrenOnly() {
        val property = propertyBuilder.from(GenericType.of(PropertyClasses.Foo::class.java))

        val visited = mutableListOf<Property<*, *>>()

        property.updateChildren(Property.Updater.Flat { visited.add(it); it })

        assertThat(visited).extracting("name").containsExactly("bar", "baz")
    }

    @Test
    fun recursiveUpdateVisitsAllContainedProperties() {
        val property = propertyBuilder.from(GenericType.of(PropertyClasses.Foo::class.java))

        val visited = mutableListOf<Property<*, *>>()

        property.update(Property.Updater.Recursive { visited.add(it); it })

        assertThat(visited).extracting("name").containsExactly("qux", "bar", "baz", "#")
    }

    @Test
    fun recursiveChildrenUpdateVisitsAllContainedProperties() {
        val property = propertyBuilder.from(GenericType.of(PropertyClasses.Foo::class.java))

        val visited = mutableListOf<Property<*, *>>()

        property.updateChildren(Property.Updater.Recursive { visited.add(it); it })

        assertThat(visited).extracting("name").containsExactly("qux", "bar", "baz")
    }

    @Test
    fun filterChildrenVisitsAllContainedProperties() {
        val property = propertyBuilder.from(GenericType.of(PropertyClasses.Foo::class.java))

        val visited = mutableListOf<Property<*, *>>()

        property.filterChildren { visited.add(it); true }

        assertThat(visited).extracting("name").containsExactly("bar", "baz", "qux")
    }

    @Test
    fun filterChildrenDoesNotVisitChildrenOfFilteredChildren() {
        val property = propertyBuilder.from(GenericType.of(PropertyClasses.Foo::class.java))

        val visited = mutableListOf<Property<*, *>>()

        property.filterChildren { visited.add(it); false }

        assertThat(visited).extracting("name").containsExactly("bar", "baz")
    }


}