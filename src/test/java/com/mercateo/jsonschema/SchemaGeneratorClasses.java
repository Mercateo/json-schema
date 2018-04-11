package com.mercateo.jsonschema;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SchemaGeneratorClasses {

    public static class Simple {
        public String foo;

        public Integer bar;

        public Long baar;

        public Float baz;

        public Double qux;

        public Boolean quux;
    }

    public static class SimpleOther {
        public Long foo;

        public Double bar;

        public Number baz;
    }

    public static class Benchmark {
        public Simple simple;

        public References references;

        public Required required;
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
    }

    public static class IntValidation {
        @Max(10)
        public Integer max;

        @Min(5)
        public Integer min;
    }

    public static class StringValidation {
        @Size(max = 10)
        public String max;

        @Size(min = 5)
        public String min;

        @Size(min = 6, max = 9)
        public String range;
    }

    public static class Superclass {
        public String foo;
    }

    public static class Subclass extends Superclass {
        public String bar;

        public String qux;
    }

    public static class IntegerAnnotations {
        @Max(value = 10)
        public Integer max;

        @Min(value = 5)
        public Integer min;
    }

    public enum Value {TRUE, FALSE}

    public static class EnumValue {
        public Value enumValue;
    }
}
