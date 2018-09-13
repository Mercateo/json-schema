package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType
import com.mercateo.jsonschema.property.collector.FieldCollector
import com.mercateo.jsonschema.property.mapper.PropertyDescriptorClasses
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test


class PropertyDescriptorTest {

    lateinit var propertyBuilder: BasicPropertyBuilder

    @Before
    fun setUp() {
        propertyBuilder = BasicPropertyBuilder(rawPropertyCollectors = listOf(FieldCollector()))
    }

    @Test
    fun flatUpdateVisitsItselfOnly() {
        val property = propertyBuilder.from(GenericType.of(PropertyDescriptorClasses.Foo::class.java))

        val visited = mutableListOf<Property<*, *>>()

        property.update( PropertyDescriptor.UpdateVisitor.Flat({visited.add(it); it }) )

        assertThat(visited).extracting("name").containsExactly("#")
    }

    @Test
    fun flatUpdateChildrenVisitsDirectChildrenOnly() {
        val property = propertyBuilder.from(GenericType.of(PropertyDescriptorClasses.Foo::class.java))

        val visited = mutableListOf<Property<*, *>>()

        property.updateChildren( PropertyDescriptor.UpdateVisitor.Flat( { visited.add(it); it }))

        assertThat(visited).extracting("name").containsExactly("bar", "baz")
    }

    @Test
    fun recursiveUpdateVisitsAllContainedProperties() {
        val property = propertyBuilder.from(GenericType.of(PropertyDescriptorClasses.Foo::class.java))

        val visited = mutableListOf<Property<*, *>>()

        property.update( PropertyDescriptor.UpdateVisitor.Recursive( { visited.add(it); it}))

        assertThat(visited).extracting("name").containsExactly("qux", "bar", "baz", "#")
    }

    @Test
    fun recursiveChildrenUpdateVisitsAllContainedProperties() {
        val property = propertyBuilder.from(GenericType.of(PropertyDescriptorClasses.Foo::class.java))

        val visited = mutableListOf<Property<*, *>>()

        property.updateChildren( PropertyDescriptor.UpdateVisitor.Recursive( { visited.add(it); it}))

        assertThat(visited).extracting("name").containsExactly("qux", "bar", "baz")
    }
}