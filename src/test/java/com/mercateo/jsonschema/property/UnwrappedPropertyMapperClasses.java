package com.mercateo.jsonschema.property;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class UnwrappedPropertyMapperClasses {

    static public class SecondLevelPropertyHolder {
        public String quux;

        @JsonUnwrapped
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

        @JsonUnwrapped
        public UnwrappedPropertyHolder unwrappedPropertyHolder;
    }

    static public class DoubleUnwrappedPropertyHolder {
        public String qux;

        @JsonUnwrapped(prefix = "baz")
        public UnwrappedPropertyHolder unwrappedPropertyHolder1;

        @JsonUnwrapped(suffix = "qux")
        public UnwrappedPropertyHolder unwrappedPropertyHolder2;
    }

    static public class UnwrappedPropertyHolder {
        public String foo;

        public String bar;
    }
}
