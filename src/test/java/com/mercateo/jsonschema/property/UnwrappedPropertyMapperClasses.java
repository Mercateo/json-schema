package com.mercateo.jsonschema.property;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class UnwrappedPropertyMapperClasses {

    @Retention(RetentionPolicy.RUNTIME)
    @interface Unwrap {
    }

    static class SecondLevelPropertyHolder {
        public String quux;

        @Unwrap
        public PropertyHolder propertyHolder;
    }

    static class PropertyHolder {
        public String qux;

        @Unwrap
        public UnwrappedPropertyHolder unwrappedPropertyHolder;
    }

    static class UnwrappedPropertyHolder {
        public String foo;

        public String bar;
    }
}
