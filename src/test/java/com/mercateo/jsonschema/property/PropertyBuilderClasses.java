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
        String property;
    }

    static enum TestEnum {VALUE_1, VALUE_2}

    static class EnumPropertyHolder {
        TestEnum enumProperty;
    }

    @Annotation1
    static class MethodPropertyHolder {
        @Annotation2
        String getProperty() {
            return "foo";
        }
    }

    static class TwoLevelMethodPropertyHolder {
        @Annotation3
        MethodPropertyHolder holder;
    }

    static class TwoLevelPropertyHolder {
        @Annotation1
        GenericPropertyHolder<String> holder;
    }

    @Annotation2
    static class GenericPropertyHolder<T> {
        T property;
    }

    static class InheritedPropertyHolder extends PropertyHolder {}

    static class CollectionPropertyHolder {
        List<String> values;
    }

    static class NestedCollectionPropertyHolder {
        List<String[]> values;
    }

    static class RecursivePropertyHolder {
        List<RecursivePropertyHolder> children;

        String name;
    }

    static class TypesPropertyHolder {
        String string;

        int integerPrimitive;

        Integer integerValue;

        long longPrimitive;

        Long longValue;

        boolean booleanPrimitive;

        Boolean booleanValue;

        float floatPrimitive;

        Float floatValue;

        double doublePrimitive;

        Double doubleValue;
    }

}
