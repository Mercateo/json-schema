package com.mercateo.jsonschema.property;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class UnwrappedPropertyBuilderClasses {

    @Retention(RetentionPolicy.RUNTIME)
    @interface Unwrap {}

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
