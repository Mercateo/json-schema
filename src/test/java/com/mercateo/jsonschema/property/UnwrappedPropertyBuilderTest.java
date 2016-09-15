package com.mercateo.jsonschema.property;

import javaslang.collection.List;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.mercateo.jsonschema.property.PropertyBuilderTest.getFirstElement;
import static org.assertj.core.api.Assertions.assertThat;

public class UnwrappedPropertyBuilderTest {

    private UnwrappedPropertyBuilder unwrappedPropertyBuilder;

    @Retention(RetentionPolicy.RUNTIME)
    static @interface Unwrap {

    }

    @Before
    public void setUp() throws Exception {
        PropertyBuilder propertyBuilder = new PropertyBuilderDefault(List.of(new FieldCollector(FieldCollectorConfig.builder()
                .build())));
        unwrappedPropertyBuilder = new UnwrappedPropertyBuilder(propertyBuilder, Unwrap.class);
    }

    @Test
    public void singleLevelUnwrap() throws Exception {
        final Property unwrappedProperty = unwrappedPropertyBuilder.from(PropertyHolder.class);

        assertThat(unwrappedProperty.children()).extracting(Property::name).containsExactlyInAnyOrder("foo", "bar");
    }

    @Test
    public void twoLevelUnwrap() throws Exception {
        final Property unwrappedProperty = unwrappedPropertyBuilder.from(SecondLevelPropertyHolder.class);

        assertThat(unwrappedProperty.children()).extracting(Property::name).containsExactlyInAnyOrder("foo", "bar");
    }

    @Test
    public void singleLevelUnwrapGetValue() throws Exception {
        final Property unwrappedProperty = unwrappedPropertyBuilder.from(PropertyHolder.class);

        final PropertyHolder propertyHolder = new PropertyHolder();
        propertyHolder.unwrappedPropertyHolder = new UnwrappedPropertyHolder();
        propertyHolder.unwrappedPropertyHolder.foo = "value1";

        final Property firstElement = getFirstElement(unwrappedProperty.children());

        assertThat(firstElement.getValue(propertyHolder)).isEqualTo("value1");
    }

    static class SecondLevelPropertyHolder {
        @Unwrap
        PropertyHolder propertyHolder;
    }

    static class PropertyHolder {
        @Unwrap
        UnwrappedPropertyHolder unwrappedPropertyHolder;
    }

    static class UnwrappedPropertyHolder {
        String foo;

        String bar;
    }
}
