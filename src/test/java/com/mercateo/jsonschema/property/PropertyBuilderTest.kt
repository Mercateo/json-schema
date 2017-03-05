package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.property.PropertyBuilderClasses.*
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.IterableAssert
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory

class PropertyBuilderTest {
    private val log = LoggerFactory.getLogger(PropertyBuilderTest::class.java)

    private lateinit var propertyBuilder: PropertyBuilder

    @Before
    @Throws(Exception::class)
    fun setUp() {
        propertyBuilder = PropertyBuilderDefault(FieldCollector(FieldCollectorConfig()), MethodCollector())
    }

    @Test
    fun rootPropertyDefaultNameIsHashCharacter() {
        val property = propertyBuilder.from(PropertyHolder::class.java)

        assertThat(property.name).isEqualTo("#")
    }

    @Test
    @Throws(Exception::class)
    fun containsChildFromField() {
        val property = propertyBuilder.from(PropertyHolder::class.java)

        assertThat(property.children).extracting("name").containsExactly("property")
    }

    @Test
    @Throws(Exception::class)
    fun buildsPropertiesRecursively() {
        val property = propertyBuilder.from(TwoLevelPropertyHolder::class.java)

        val children = property.children
        val firstLevelElement = children.first()
        val secondLevelElement = firstLevelElement.children.first()

        assertThat(secondLevelElement.name).isEqualTo("property")
    }

    @Test
    @Throws(Exception::class)
    fun createsPropertyWithGenerics() {

        val property = propertyBuilder.from(TwoLevelPropertyHolder::class.java)

        val firstElement = property.children.first()
        val secondLevelElement = firstElement.children.first()
        assertThat(secondLevelElement.genericType.rawType).isEqualTo(String::class.java)
    }

    @Test
    @Throws(Exception::class)
    fun propertyReturnsPropertyValue() {
        val property = propertyBuilder.from(PropertyHolder::class.java)

        val firstElement = property.children.first()

        val propertyHolder = PropertyHolder()
        propertyHolder.property = "foo"

        assertThat(firstElement.getValue(propertyHolder)).isEqualTo("foo")
    }

    @Test
    @Throws(Exception::class)
    fun propertyReturnsClassAnnotation() {
        val property = propertyBuilder.from(PropertyHolder::class.java)

        val firstElement = property.annotations.values.first().first()
        assertThat(firstElement).isInstanceOf(Annotation1::class.java)
    }

    @Test
    @Throws(Exception::class)
    fun subPropertyReturnsFieldAnnotation() {
        val property = propertyBuilder.from(TwoLevelPropertyHolder::class.java)

        val firstElement1 = property.children.first()
        assertThat(getAnnotations(firstElement1)).containsExactlyInAnyOrder(Annotation1::class.java, Annotation2::class.java)
    }

    fun <T> assertThat(actual: Iterable<T>) = IterableAssert(actual)

    private fun getAnnotations(firstElement1: Property<*,*>): Set<Class<out Annotation>> {
        return firstElement1.annotations.keys
    }

    @Test
    @Throws(Exception::class)
    fun returnInheritedProperty() {
        val property = propertyBuilder.from(PropertyBuilderClasses.InheritedPropertyHolder::class.java)

        assertThat(property.children).extracting("name").containsExactly("property")
    }

    @Test
    @Throws(Exception::class)
    fun nonObjectTypesShouldHaveNoChildren() {
        val property = propertyBuilder.from(PropertyHolder::class.java)

        val firstElement = property.children.first()
        assertThat(firstElement.children).isEmpty()
    }

    @Test
    @Throws(Exception::class)
    fun returnsIdentialTypeDescriptorsForSameType() {
        val property1 = propertyBuilder.from(PropertyHolder::class.java)
        val property2 = propertyBuilder.from(PropertyHolder::class.java)

        assertThat(property1).isNotSameAs(property2)
        assertThat(property1.propertyDescriptor).isSameAs(property2.propertyDescriptor)
    }

    @Test
    @Throws(Exception::class)
    fun returnMethodProperty() {
        val property = propertyBuilder.from(MethodPropertyHolder::class.java)
        val firstElement = property.children.first()
        assertThat(firstElement.name).isEqualTo("property")
    }

    @Test
    @Throws(Exception::class)
    fun returnMethodPropertyValue() {
        val property = propertyBuilder.from(MethodPropertyHolder::class.java)
        val firstElement = property.children.first()

        val methodPropertyHolder = MethodPropertyHolder()
        assertThat(firstElement.getValue(methodPropertyHolder)).isEqualTo("foo")
    }

    @Test
    @Throws(Exception::class)
    fun returnMethodAnnotations() {
        val property = propertyBuilder.from(MethodPropertyHolder::class.java)
        val firstElement = property.children.first()

        assertThat(getAnnotations(firstElement)).containsExactly(Annotation2::class.java)
    }

    @Test
    @Throws(Exception::class)
    fun returnMethodAndClassAnnotations() {
        val property = propertyBuilder.from(TwoLevelMethodPropertyHolder::class.java)
        val firstElement = property.children.first()

        assertThat(getAnnotations(firstElement)).containsExactlyInAnyOrder(Annotation1::class.java, Annotation3::class.java)
    }

    @Test
    @Throws(Exception::class)
    fun followCollectionProperties() {
        val property: Property<Void, CollectionPropertyHolder> = propertyBuilder.from(CollectionPropertyHolder::class.java)
        val collectionElement: Property<CollectionPropertyHolder, Any> = property.children.first()
        val collectionTypeElement: Property<Any, out Any?> = collectionElement.children.first()

        assertThat(collectionTypeElement).isNotNull()
        assertThat(collectionTypeElement.name).isEqualTo("")
        assertThat(collectionTypeElement.annotations.values).isEmpty()
        assertThat(collectionTypeElement.genericType.rawType).isEqualTo(String::class.java)

        val collectionPropertyHolder = CollectionPropertyHolder()
        collectionPropertyHolder.values = listOf("foo")

        assertThat(collectionTypeElement.getValue(collectionPropertyHolder)).isNull()
    }

    @Test
    @Throws(Exception::class)
    fun followNestedCollectionProperties() {
        val property = propertyBuilder.from(NestedCollectionPropertyHolder::class.java)
        val collectionElement = property.children.first()

        val collectionTypeElement = collectionElement.children.first()
        assertThat(collectionTypeElement).isNotNull()
        assertThat(collectionTypeElement.name).isEqualTo("")
        assertThat(collectionTypeElement.annotations.values).isEmpty()
        assertThat(collectionTypeElement.genericType.rawType).isEqualTo(Array<String>::class.java)

        val nestedCollectionTypeElement = collectionTypeElement.children.first()

        assertThat(nestedCollectionTypeElement).isNotNull()
        assertThat(nestedCollectionTypeElement.name).isEqualTo("")
        assertThat(nestedCollectionTypeElement.genericType.rawType).isEqualTo(String::class.java)
        assertThat(nestedCollectionTypeElement.annotations.values).isEmpty()
    }

    @Test
    @Throws(Exception::class)
    fun returnsPropertyWithoutChildrenIfRecursive() {
        val property = propertyBuilder.from(RecursivePropertyHolder::class.java)
        val childrenIterator = property.children.iterator()
        val firstElement = childrenIterator.next()

        assertThat(firstElement.name).isEqualTo("children")

        val firstSubelement = firstElement.children.first()
        assertThat(firstSubelement.name).isEmpty()
        assertThat(firstSubelement.genericType.rawType).isEqualTo(RecursivePropertyHolder::class.java)
        val propertyDescriptor = firstSubelement.propertyDescriptor
        assertThat(propertyDescriptor.children).isEmpty()

        val secondElement = childrenIterator.next()
        assertThat(secondElement.name).isEqualTo("name")
        assertThat(secondElement.genericType.rawType).isEqualTo(String::class.java)
    }


}
