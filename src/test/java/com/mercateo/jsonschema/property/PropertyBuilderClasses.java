package com.mercateo.jsonschema.property;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class PropertyBuilderClasses {
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
    static class PropertyHolder {
        public String property;
    }

    static enum TestEnum {VALUE_1, VALUE_2}

    static class EnumPropertyHolder {
        public TestEnum enumProperty;
    }

    @Annotation1
    static class MethodPropertyHolder {
        @Annotation2
        public String getProperty() {
            return "foo";
        }
    }

    static class TwoLevelMethodPropertyHolder {
        @Annotation3
        public MethodPropertyHolder holder;
    }

    static class TwoLevelPropertyHolder {
        @Annotation1
        public GenericPropertyHolder<String> holder;
    }

    @Annotation2
    static class GenericPropertyHolder<T> {
        public T property;
    }

    static class InheritedPropertyHolder extends PropertyHolder {}

    static class CollectionPropertyHolder {
        public List<String> values;
    }

    static class NestedCollectionPropertyHolder {
        public List<String[]> values;
    }

    static class RecursivePropertyHolder {
        public List<RecursivePropertyHolder> children;

        public String name;
    }

    static class TypesPropertyHolder {
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
