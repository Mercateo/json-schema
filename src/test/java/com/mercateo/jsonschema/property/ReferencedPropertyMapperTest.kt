package com.mercateo.jsonschema.property

import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Before
import org.junit.Test


class ReferencedPropertyMapperTest {

    private lateinit var propertyBuilder: PropertyBuilderWrapper

    @Before
    fun setUp() {
        propertyBuilder = PropertyBuilderWrapper(
                PropertyBuilderDefault(FieldCollector(FieldCollectorConfig())),
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

        val listType = listChild.children.first()
        assertThat(listType.children).isEmpty()
        assertThat(listType.name).isEqualTo("")
        assertThat(listType.path).isEqualTo("#/children/")
        assertThat(listType.reference).isEqualTo("#")
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

        val holder2 = property.children.find { it.name == "holder2" }!!
        assertThat(holder2.children).isEmpty()
        assertThat(holder2.name).isEqualTo("holder2")
        assertThat(holder2.path).isEqualTo("#/holder2")
        assertThat(holder2.reference).isEqualTo("#/holder1")
    }
}