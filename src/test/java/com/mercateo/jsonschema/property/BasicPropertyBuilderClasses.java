package com.mercateo.jsonschema.property;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Optional;

public class BasicPropertyBuilderClasses {
    @Retention(RetentionPolicy.RUNTIME)
    @interface Annotation1 {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Annotation2 {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Annotation3 {
    }

    @Annotation1
    static public class PropertyHolder {
        public String property;
    }

    static public class OptionalPropertyHolder {
        public Optional<String> property;
    }

    public enum TestEnum {VALUE_1, VALUE_2}

    static public class EnumPropertyHolder {
        public TestEnum enumProperty;
    }

    @Annotation1
    static public class MethodPropertyHolder {
        @Annotation2
        public String getProperty() {
            return "foo";
        }
    }

    static public class TwoLevelMethodPropertyHolder {
        @Annotation3
        public MethodPropertyHolder holder;
    }

    static public class TwoLevelPropertyHolder {
        @Annotation1
        public GenericPropertyHolder<String> holder;
    }

    @Annotation2
    static public class GenericPropertyHolder<T> {
        public T property;
    }

    static public class InheritedPropertyHolder extends PropertyHolder {}

    static public class CollectionPropertyHolder {
        public List<String> values;
    }

    static public class NestedCollectionPropertyHolder {
        public List<String[]> values;
    }

    static public class RecursivePropertyHolder {
        public List<RecursivePropertyHolder> children;

        public String name;
    }

    static public class TypesPropertyHolder {
        public String string;

        public int integerPrimitive;

        public Integer integerValue;

        public long longPrimitive;

        public Long longValue;

        public boolean booleanPrimitive;

        public Boolean booleanValue;

        public float floatPrimitive;

        public Float floatValue;

        public double doublePrimitive;

        public Double doubleValue;
    }

}
