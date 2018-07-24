package com.mercateo.jsonschema.property.mapper

import com.mercateo.jsonschema.property.BasicPropertyBuilder
import com.mercateo.jsonschema.property.PropertyBuilderWrapper
import com.mercateo.jsonschema.property.PropertyDescriptor
import com.mercateo.jsonschema.property.collector.FieldCollector
import com.mercateo.jsonschema.property.from
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test


class ReferencedPropertyMapperTest {

    private lateinit var propertyBuilder: PropertyBuilderWrapper

    @Before
    fun setUp() {
        propertyBuilder = PropertyBuilderWrapper(
                BasicPropertyBuilder(emptyMap(), FieldCollector()),
                ReferencedPropertyMapper()
        )
    }

    @Test
    fun holderWithSingleElementShouldNotHaveReferenceSet() {
        val property = propertyBuilder.from(ReferencedPropertyMapperClasses.PropertyHolder::class.java)

        assertThat(property.children).hasSize(1)
        assertThat(property.name).isEqualTo("#")
        assertThat(property.path).isEqualTo("#")
        assertThat(property.reference).isNull()

        val child = property.children.first();
        assertThat(child.children).isEmpty()
        assertThat(child.name).isEqualTo("property")
        assertThat(child.path).isEqualTo("#/property")
        assertThat(child.reference).isNull()
        assertThat(child.propertyDescriptor.context).isInstanceOf(PropertyDescriptor.Context.Children::class.java)
    }

    @Test
    fun recursiveIdenticalElementsShouldBeMappedToReference() {
        val property = propertyBuilder.from(ReferencedPropertyMapperClasses.RecursivePropertyHolder::class.java)

        assertThat(property.children).hasSize(2)
        assertThat(property.reference).isNull()
        assertThat(property.name).isEqualTo("#")
        assertThat(property.path).isEqualTo("#")

        val listChild = property.children.find { it.name == "children" }!!
        assertThat(listChild.children).hasSize(1)
        assertThat(listChild.name).isEqualTo("children")
        assertThat(listChild.path).isEqualTo("#/children")
        assertThat(listChild.reference).isNull()
        assertThat(listChild.propertyDescriptor.context).isInstanceOf(PropertyDescriptor.Context.Children::class.java)

        val listType = listChild.children.first()
        assertThat(listType.children).isEmpty()
        assertThat(listType.name).isEqualTo("")
        assertThat(listType.path).isEqualTo("#/children/")
        assertThat(listType.reference).isEqualTo("#")
        assertThat(listType.propertyDescriptor.context).isInstanceOf(PropertyDescriptor.Context.InnerReference::class.java)
    }

    @Test
    fun moreThanOneIdenticalElementsShouldBeMappedToReference() {
        val property = propertyBuilder.from(ReferencedPropertyMapperClasses.IdenticalPropertyHolder::class.java)

        assertThat(property.children).hasSize(2)
        assertThat(property.reference).isNull()
        assertThat(property.name).isEqualTo("#")
        assertThat(property.path).isEqualTo("#")

        val holder1 = property.children.find { it.name == "holder1" }!!
        assertThat(holder1.children).hasSize(1)
        assertThat(holder1.name).isEqualTo("holder1")
        assertThat(holder1.path).isEqualTo("#/holder1")
        assertThat(holder1.reference).isNull()
        assertThat(holder1.propertyDescriptor.context).isInstanceOf(PropertyDescriptor.Context.Children::class.java)

        val holder2 = property.children.find { it.name == "holder2" }!!
        assertThat(holder2.children).isEmpty()
        assertThat(holder2.name).isEqualTo("holder2")
        assertThat(holder2.path).isEqualTo("#/holder2")
        assertThat(holder2.reference).isEqualTo("#/holder1")
        assertThat(holder2.propertyDescriptor.context).isInstanceOf(PropertyDescriptor.Context.InnerReference::class.java)
    }
}