package com.mercateo.jsonschema.schema;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public static class References {
        public Element foo;

        public Element bar;
    }

    public static class Element {
        public UUID id;

        public String name;
    }

    public static class Required {
        public String foo;

        @NotNull
        public String bar;

        @NotNull
        public Optional<String> baz;

        @org.hibernate.validator.constraints.NotEmpty
        public String qux;

        @NotEmpty
        public String quux;
    }
}
