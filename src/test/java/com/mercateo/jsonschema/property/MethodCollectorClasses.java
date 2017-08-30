package com.mercateo.jsonschema.property;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MethodCollectorClasses {
    static class StaticMethod {
        String getString() {
            return "foo";
        }

        static Integer getNumber() {
            return 0;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface MethodAnnotation {
    }

    static abstract class SuperClass {
        @MethodAnnotation
        public abstract String getFoo();
    }

    static class SubClass extends SuperClass {
        @Override
        public String getFoo() {
            return "foo";
        }

        public String getBar() {
            return "bar";
        }
    }

    interface MethodInterface {
        @MethodAnnotation
        String getFoo();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface OtherMethodAnnotation {
    }

    interface OtherMethodInterface {
        @OtherMethodAnnotation
        String getFoo();
    }

    static class ImplementingClass implements MethodInterface, OtherMethodInterface {
        @Override
        public String getFoo() {
            return "foo";
        }

        public String getBar() {
            return "bar";
        }
    }
}
