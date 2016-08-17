package com.mercateo.jsonschema.property;

import javaslang.collection.List;
import org.junit.Test;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodCollectorTest {
    @Test
    public void collectMethods() {
        final MethodCollector methodCollector = new MethodCollector();
        final List<RawProperty> rawProperties = methodCollector.forType(TestClass.class).toList();

        assertThat(rawProperties).extracting(RawProperty::name).containsExactlyInAnyOrder("value", "number", "enabled", "running");
    }

    static class TestClass {

        void setValue(Float value) {

        }

        Float getValue() {
            return null;
        }

        void addNumber(Double number) {

        }

        Double number() {
            return null;
        }

        void setEnabled(Boolean enabled) {

        }

        Boolean isEnabled() {
            return null;
        }

        void setRuning() {

        }

        boolean running() {
            return false;
        }
    }
}
