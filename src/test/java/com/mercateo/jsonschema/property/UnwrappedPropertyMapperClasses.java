package com.mercateo.jsonschema.property;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class UnwrappedPropertyMapperClasses {

    @Retention(RetentionPolicy.RUNTIME)
    @interface Unwrap {}

    static class SecondLevelPropertyHolder {
        @Unwrap
        public PropertyHolder propertyHolder;
    }

    static class PropertyHolder {
        @Unwrap
        public UnwrappedPropertyHolder unwrappedPropertyHolder;
    }

    static class UnwrappedPropertyHolder {
        public String foo;

        public String bar;
    }
}
