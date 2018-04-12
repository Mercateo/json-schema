package com.mercateo.jsonschema.property;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class UnwrappedPropertyMapperClasses {

    @Retention(RetentionPolicy.RUNTIME)
    @interface Unwrap {
    }

    static public class SecondLevelPropertyHolder {
        public String quux;

        @Unwrap
        public PropertyHolder propertyHolder;
    }

    static public class WrappedPropertyHolder1 {
        public PropertyHolder holder;
    }

    static public class WrappedPropertyHolder2 {
        public PropertyHolder holder;
    }

    static public class PropertyHolder {
        public String qux;

        @Unwrap
        public UnwrappedPropertyHolder unwrappedPropertyHolder;
    }

    static public class UnwrappedPropertyHolder {
        public String foo;

        public String bar;
    }
}
