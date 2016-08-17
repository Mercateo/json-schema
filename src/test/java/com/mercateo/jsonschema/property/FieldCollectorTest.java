package com.mercateo.jsonschema.property;

import com.mercateo.jsonschema.generictype.GenericType;
import javaslang.collection.List;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldCollectorTest {

    private FieldCollector fieldCollector;

    @Before
    public void setUp() throws Exception {
        fieldCollector = new FieldCollector(FieldCollectorConfig.builder().build());
    }

    @Test
    public void mapAllDeclaredFields() throws Exception {
        final List<RawProperty> properties = fieldCollector.forType(PropertyHolder.class).toList();

        assertThat(properties).extracting(RawProperty::name).containsExactlyInAnyOrder("hidden",
                "visible");
    }

    @Test
    public void limitToPublicFieldsIfConfigured() throws Exception {
        fieldCollector = new FieldCollector(FieldCollectorConfig.builder().withIncludePrivateFields(false).build());
        final List<RawProperty> properties = fieldCollector.forType(PropertyHolder.class).toList();

        assertThat(properties).extracting(RawProperty::name).containsExactly("visible");
    }

    @Test
    public void mapAllDaeclaredFields() throws Exception {
        final RawProperty hidden = fieldCollector.forType(PropertyHolder.class)
                .filter(p -> p.name().equals("hidden")).head();

        final PropertyHolder propertyHolder = new PropertyHolder();
        hidden.valueAccessor().apply(propertyHolder);
    }

    static class PropertyHolder {
        public String visible;

        private String hidden;
    }
}
