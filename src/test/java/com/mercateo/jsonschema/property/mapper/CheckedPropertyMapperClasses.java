package com.mercateo.jsonschema.property.mapper;

public class CheckedPropertyMapperClasses {
    public static class Simple {
        public String foo;

        public String bar;
    }

    public static class Nested {
        public Simple first;

        public Simple second;
    }
}
