package com.mercateo.jsonschema.property;

import javaslang.collection.Iterator;
import javaslang.collection.List;
import javaslang.collection.Set;
import javaslang.collection.Traversable;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PropertyBuilderTest {
    private Logger log = LoggerFactory.getLogger(PropertyBuilderTest.class);

    private PropertyBuilder propertyBuilder;

    public static <T> T getFirstElement(Traversable<T> collection) {
        return collection.iterator().next();
    }

    @Before
    public void setUp() throws Exception {
        propertyBuilder = new PropertyBuilderDefault(List.of(new FieldCollector(FieldCollectorConfig.builder().build()), new MethodCollector()));
    }

    @Test
    public void rootPropertyDefaultNameIsHashCharacter() {
        Property property = propertyBuilder.from(PropertyHolder.class);

        assertThat(property.name()).isEqualTo("#");
    }

    @Test
    public void callingValueAccessorOfRootElementThrows() {
        Property property = propertyBuilder.from(PropertyHolder.class);

        assertThatThrownBy(() -> property.getValue(null)).isInstanceOf(IllegalStateException.class)
                .hasMessage("cannot call value accessor for root element");
    }

    @Test
    public void containsChildFromField() throws Exception {
        Property property = propertyBuilder.from(PropertyHolder.class);

        assertThat(property.children()).extracting(Property::name).containsExactly("property");
    }

    @Test
    public void buildsPropertiesRecursively() throws Exception {
        Property property = propertyBuilder.from(TwoLevelPropertyHolder.class);

        final List<Property> children = property.children();
        final Property firstLevelElement = getFirstElement(children);
        final Property secondLevelElement = getFirstElement(firstLevelElement.children());

        assertThat(secondLevelElement.name()).isEqualTo("property");
    }

    @Test
    public void createsPropertyWithGenerics() throws Exception {

        Property property = propertyBuilder.from(TwoLevelPropertyHolder.class);

        final Property firstElement = getFirstElement(property.children());
        final Property secondLevelElement = getFirstElement(firstElement.children());
        assertThat(secondLevelElement.genericType().getRawType()).isEqualTo(String.class);
    }

    @Test
    public void propertyReturnsPropertyValue() throws Exception {
        Property property = propertyBuilder.from(PropertyHolder.class);

        final Property firstElement = getFirstElement(property.children());

        final PropertyHolder propertyHolder = new PropertyHolder();
        propertyHolder.property = "foo";

        assertThat(firstElement.getValue(propertyHolder)).isEqualTo("foo");
    }

    @Test
    public void propertyReturnsClassAnnotation() throws Exception {
        Property property = propertyBuilder.from(PropertyHolder.class);

        final Annotation firstElement = getFirstElement(property.annotations().head()._2());
        assertThat(firstElement).isInstanceOf(Annotation1.class);
    }

    @Test
    public void subPropertyReturnsFieldAnnotation() throws Exception {
        Property property = propertyBuilder.from(TwoLevelPropertyHolder.class);

        final Property firstElement1 = getFirstElement(property.children());
        assertThat(getAnnotations(firstElement1).toJavaSet()).containsExactlyInAnyOrder(Annotation1.class, Annotation2.class);
    }

    private Set<Class<? extends Annotation>> getAnnotations(Property firstElement1) {
        return firstElement1.annotations().keySet();
    }

    @Test
    public void returnInheritedProperty() throws Exception {
        Property property = propertyBuilder.from(InheritedPropertyHolder.class);

        assertThat(property.children()).extracting(Property::name).containsExactly("property");
    }

    @Test
    public void nonObjectTypesShouldHaveNoChildren() throws Exception {
        Property property = propertyBuilder.from(PropertyHolder.class);

        final Property firstElement = getFirstElement(property.children());
        assertThat(firstElement.children()).isEmpty();
    }

    @Test
    public void returnsIdentialTypeDescriptorsForSameType() throws Exception {
        Property property1 = propertyBuilder.from(PropertyHolder.class);
        Property property2 = propertyBuilder.from(PropertyHolder.class);

        assertThat(property1).isNotSameAs(property2);
        assertThat(property1.propertyDescriptor()).isSameAs(property2.propertyDescriptor());
    }

    @Test
    public void returnMethodProperty() throws Exception {
        Property property = propertyBuilder.from(MethodPropertyHolder.class);
        final Property firstElement = getFirstElement(property.children());
        assertThat(firstElement.name()).isEqualTo("property");
    }

    @Test
    public void returnMethodPropertyValue() throws Exception {
        Property property = propertyBuilder.from(MethodPropertyHolder.class);
        final Property firstElement = getFirstElement(property.children());

        final MethodPropertyHolder methodPropertyHolder = new MethodPropertyHolder();
        assertThat(firstElement.getValue(methodPropertyHolder)).isEqualTo("foo");
    }

    @Test
    public void returnMethodAnnotations() throws Exception {
        Property property = propertyBuilder.from(MethodPropertyHolder.class);
        final Property firstElement = getFirstElement(property.children());

        assertThat(getAnnotations(firstElement)).containsExactly(Annotation2.class);
    }

    @Test
    public void returnMethodAndClassAnnotations() throws Exception {
        Property property = propertyBuilder.from(TwoLevelMethodPropertyHolder.class);
        final Property firstElement = getFirstElement(property.children());

        assertThat(getAnnotations(firstElement)).containsExactlyInAnyOrder(Annotation1.class, Annotation3.class);
    }

    @Test
    public void followCollectionProperties() throws Exception {
        Property property = propertyBuilder.from(CollectionPropertyHolder.class);
        final Property collectionElement = getFirstElement(property.children());
        final Property collectionTypeElement = getFirstElement(collectionElement.children());

        assertThat(collectionTypeElement).isNotNull();
        assertThat(collectionTypeElement.name()).isEqualTo("");
        assertThat(collectionTypeElement.annotations().values()).isEmpty();
        assertThat(collectionTypeElement.genericType().getRawType()).isEqualTo(String.class);

        final CollectionPropertyHolder collectionPropertyHolder = new CollectionPropertyHolder();
        collectionPropertyHolder.values = List.of("foo");

        assertThat(collectionTypeElement.getValue(collectionPropertyHolder)).isNull();
    }

    @Test
    public void followNestedCollectionProperties() throws Exception {
        Property property = propertyBuilder.from(NestedCollectionPropertyHolder.class);
        final Property collectionElement = getFirstElement(property.children());

        final Property collectionTypeElement = getFirstElement(collectionElement.children());
        assertThat(collectionTypeElement).isNotNull();
        assertThat(collectionTypeElement.name()).isEqualTo("");
        assertThat(collectionTypeElement.annotations().values()).isEmpty();
        assertThat(collectionTypeElement.genericType().getRawType()).isEqualTo(String[].class);

        final Property nestedCollectionTypeElement = getFirstElement(collectionTypeElement.children());

        assertThat(nestedCollectionTypeElement).isNotNull();
        assertThat(nestedCollectionTypeElement.name()).isEqualTo("");
        assertThat(nestedCollectionTypeElement.genericType().getRawType()).isEqualTo(String.class);
        assertThat(nestedCollectionTypeElement.annotations().values()).isEmpty();
    }

    @Test
    public void returnPropertyReferenceIfRecursive() throws Exception {
        Property property = propertyBuilder.from(RecursivePropertyHolder.class);
        final Iterator<Property> childrenIterator = property.children().iterator();
        final Property firstElement = childrenIterator.next();

        assertThat(firstElement.name()).isEqualTo("children");

        final Property firstSubelement = getFirstElement(firstElement.children() );
        assertThat(firstSubelement.name()).isEmpty();
        assertThat(firstSubelement.genericType().getRawType()).isEqualTo(RecursivePropertyHolder.class);
        assertThat(firstSubelement.children()).isEmpty();
        final PropertyDescriptor propertyDescriptor = firstSubelement.propertyDescriptor();
        // TODO fix test
        //assertThat(propertyDescriptor.children()).hasSize(2);

        final Property secondElement = childrenIterator.next();
        assertThat(secondElement.name()).isEqualTo("name");
        assertThat(secondElement.genericType().getRawType()).isEqualTo(String.class);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Annotation1 {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Annotation2 {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Annotation3 {
    }

    @Annotation1
    static class PropertyHolder {
        String property;
    }

    @Annotation1
    static class MethodPropertyHolder {
        @Annotation2
        String getProperty() {
            return "foo";
        }
    }

    static class TwoLevelMethodPropertyHolder {
        @Annotation3
        MethodPropertyHolder holder;
    }

    static class TwoLevelPropertyHolder {
        @Annotation1
        GenericPropertyHolder<String> holder;
    }

    @Annotation2
    static class GenericPropertyHolder<T> {
        T property;
    }

    static class InheritedPropertyHolder extends PropertyHolder {
    }

    static class CollectionPropertyHolder {
        List<String> values;
    }

    static class NestedCollectionPropertyHolder {
        List<String[]> values;
    }

    static class RecursivePropertyHolder {
        List<RecursivePropertyHolder> children;

        String name;
    }
}
