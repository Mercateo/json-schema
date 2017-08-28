package com.mercateo.jsonschema.schema;

import java.util.List;
import java.util.Optional;

public class SchemaGeneratorClasses {

    public static class Simple {
        public String foo;

        public Integer bar;

        public Float baz;

        public Boolean qux;
    }

    public static class OptionTypes {
        public Optional<String> optionalString;
    }

    public static class Collections {
        public List<String[]> strings;
    }
}
