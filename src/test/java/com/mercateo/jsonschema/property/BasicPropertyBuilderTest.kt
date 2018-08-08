package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.property.BasicPropertyBuilderClasses.*
import com.mercateo.jsonschema.property.collector.FieldCollector
import com.mercateo.jsonschema.property.collector.MethodCollector
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.*

class BasicPropertyBuilderTest {
    private val log = LoggerFactory.getLogger(BasicPropertyBuilderTest::class.java)

    private lateinit var propertyBuilder: PropertyBuilder

    @Before
    fun setUp() {
        propertyBuilder = BasicPropertyBuilder(emptyMap(), FieldCollector(), MethodCollector())
    }

    @Test
    fun rootPropertyDefaultNameIsHashCharacter() {
        val property = propertyBuilder.from(PropertyHolder::class.java)

        assertThat(property.name).isEqualTo("#")
    }

    @Test
    fun containsChildFromField() {
        val property = propertyBuilder.from(PropertyHolder::class.java)

        assertThat(property.children).extracting("name").containsExactly("property")
    }

    @Test
    fun unwrapsOptionalTypes() {
        val property = propertyBuilder.from(OptionalPropertyHolder::class.java)

        assertThat(property.children).extracting("name").containsExactly("property")
        assertThat(property.children).extracting("propertyType").containsExactly(PropertyType.STRING)
    }

    @Test
    fun unwrapsCustomTypes() {
        propertyBuilder = BasicPropertyBuilder(mapOf(Pair(GenericPropertyHolder::class.java, { wrapper ->
            if (wrapper is GenericPropertyHolder<*>) {
                wrapper.property
            } else {
                null
            }
        })), FieldCollector())
        val property = propertyBuilder.from(TwoLevelPropertyHolder::class.java)

        assertThat(property.children).extracting("name").containsExactly("holder")
        assertThat(property.children).extracting("propertyType").containsExactly(PropertyType.STRING)
    }

    @Test
    fun accessValueOfExistingOptionalType() {
        val value = OptionalPropertyHolder().also { it.property = Optional.of("foo") }

        val rootProperty = propertyBuilder.from(OptionalPropertyHolder::class.java)
        val optionalProperty: Property<OptionalPropertyHolder, Any>? = rootProperty.children.find { it.name == "property" }
        val propertyValue = optionalProperty?.getValue(value)

        assertThat(propertyValue).isEqualTo("foo")
    }

    @Test
    fun accessValueOfEmptyOptionalType() {
        val value = OptionalPropertyHolder().also { it.property = Optional.empty() }

        val rootProperty = propertyBuilder.from(OptionalPropertyHolder::class.java)
        val optionalProperty: Property<OptionalPropertyHolder, Any>? = rootProperty.children.find { it.name == "property" }
        val propertyValue = optionalProperty?.getValue(value)

        assertThat(propertyValue).isNull()
    }

    @Test
    fun containsEnumChildAsString() {
        val property = propertyBuilder.from(EnumPropertyHolder::class.java)

        assertThat(property.children).extracting("name").containsExactly("enumProperty")
        assertThat(property.children).extracting("propertyType").containsExactly(PropertyType.STRING)
    }

    @Test
    fun buildsPropertiesRecursively() {
        val property = propertyBuilder.from(TwoLevelPropertyHolder::class.java)

        val children = property.children
        val firstLevelElement = children.first()
        val secondLevelElement = firstLevelElement.children.first()

        assertThat(secondLevelElement.name).isEqualTo("property")
    }

    @Test
    fun createsPropertyWithGenerics() {

        val property = propertyBuilder.from(TwoLevelPropertyHolder::class.java)

        val firstElement = property.children.first()
        val secondLevelElement = firstElement.children.first()
        assertThat(secondLevelElement.genericType.rawType).isEqualTo(String::class.java)
    }

    @Test
    fun propertyReturnsPropertyValue() {
        val property = propertyBuilder.from(PropertyHolder::class.java)

        val firstElement = property.children.first()

        val propertyHolder = PropertyHolder()
        propertyHolder.property = "foo"

        assertThat(firstElement.getValue(propertyHolder)).isEqualTo("foo")
    }

    @Test
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

    private fun getAnnotations(firstElement1: Property<*, *>): Set<Class<out Annotation>> {
        return firstElement1.annotations.keys
    }

    @Test
    @Throws(Exception::class)
    fun returnInheritedProperty() {
        val property = propertyBuilder.from(BasicPropertyBuilderClasses.InheritedPropertyHolder::class.java)

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

    @Test
    fun shouldMapStringType() {
        val property = propertyBuilder.from(TypesPropertyHolder::class.java)

        assertThat(property.children.find { it.name == "string" }).extracting("propertyType").contains(PropertyType.STRING)
    }

    @Test
    fun shouldMapPrimitiveBooleanType() {
        val property = propertyBuilder.from(TypesPropertyHolder::class.java)

        assertThat(property.children.find { it.name == "booleanPrimitive" }).extracting("propertyType").contains(PropertyType.BOOLEAN)
    }

    @Test
    fun shouldMapBooleanType() {
        val property = propertyBuilder.from(TypesPropertyHolder::class.java)

        assertThat(property.children.find { it.name == "booleanValue" }).extracting("propertyType").contains(PropertyType.BOOLEAN)
    }

    @Test
    fun shouldMapPrimitiveIntegerType() {
        val property = propertyBuilder.from(TypesPropertyHolder::class.java)

        assertThat(property.children.find { it.name == "integerPrimitive" }).extracting("propertyType").contains(PropertyType.INTEGER)
    }

    @Test
    fun shouldMapIntegerType() {
        val property = propertyBuilder.from(TypesPropertyHolder::class.java)

        assertThat(property.children.find { it.name == "integerValue" }).extracting("propertyType").contains(PropertyType.INTEGER)
    }

    @Test
    fun shouldMapPrimitiveLongType() {
        val property = propertyBuilder.from(TypesPropertyHolder::class.java)

        assertThat(property.children.find { it.name == "longPrimitive" }).extracting("propertyType").contains(PropertyType.INTEGER)
    }

    @Test
    fun shouldMapLongType() {
        val property = propertyBuilder.from(TypesPropertyHolder::class.java)

        assertThat(property.children.find { it.name == "longValue" }).extracting("propertyType").contains(PropertyType.INTEGER)
    }

    @Test
    fun shouldMapPrimitiveFloatType() {
        val property = propertyBuilder.from(TypesPropertyHolder::class.java)

        assertThat(property.children.find { it.name == "floatPrimitive" }).extracting("propertyType").contains(PropertyType.NUMBER)
    }

    @Test
    fun shouldMapFloatType() {
        val property = propertyBuilder.from(TypesPropertyHolder::class.java)

        assertThat(property.children.find { it.name == "floatValue" }).extracting("propertyType").contains(PropertyType.NUMBER)
    }

    @Test
    fun shouldMapPrimitiveDoubleType() {
        val property = propertyBuilder.from(TypesPropertyHolder::class.java)

        assertThat(property.children.find { it.name == "doublePrimitive" }).extracting("propertyType").contains(PropertyType.NUMBER)
    }

    @Test
    fun shouldMapDoubleType() {
        val property = propertyBuilder.from(TypesPropertyHolder::class.java)

        assertThat(property.children.find { it.name == "doubleValue" }).extracting("propertyType").contains(PropertyType.NUMBER)
    }

    @Test
    fun shouldMapPolymorphicType() {
        val property = propertyBuilder.from(BasicPropertyBuilderClasses.Contact::class.java)

        assertThat(property.propertyType).isEqualTo(PropertyType.POLY)
    }

    @Test
    fun shouldMapPolymorphicProperty() {
        val property = propertyBuilder.from(BasicPropertyBuilderClasses.Polymorphism::class.java)

        val contact = property.children.find { it.name == "contact" }
        assertThat(contact?.propertyType).isEqualTo(PropertyType.POLY)

        val polymorphicTypes = contact?.propertyDescriptor?.polymorphicSubTypes.orEmpty()
        assertThat(polymorphicTypes).isNotEmpty()
        assertThat(polymorphicTypes).extracting("name").contains("email")
        assertThat(polymorphicTypes).anySatisfy {
            assertThat(it?.name).isEqualTo("fax")
            assertThat(it?.propertyType).isEqualTo(PropertyType.OBJECT)
            assertThat(it?.genericType?.rawType).isSameAs(FaxContact::class.java)
        }
        assertThat(polymorphicTypes).anySatisfy {
            assertThat(it?.name).isEqualTo("empty")
            assertThat(it?.propertyType).isEqualTo(PropertyType.OBJECT)
            assertThat(it?.genericType?.rawType).isSameAs(EmptyContact::class.java)
            assertThat(it?.children).isEmpty()
        }
    }
}
