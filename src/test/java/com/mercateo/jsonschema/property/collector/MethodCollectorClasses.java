package com.mercateo.jsonschema.property.collector;

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

    static public abstract class SuperClass {
        @MethodAnnotation
        public abstract String getFoo();
    }

    static public class SubClass extends SuperClass {
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

    public interface OtherMethodInterface {
        @OtherMethodAnnotation
        String getFoo();
    }

    static public class ImplementingClass implements MethodInterface, OtherMethodInterface {
        @Override
        public String getFoo() {
            return "foo";
        }

        public String getBar() {
            return "bar";
        }
    }
}
