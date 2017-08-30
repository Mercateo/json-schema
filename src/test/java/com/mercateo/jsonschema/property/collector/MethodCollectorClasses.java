package com.mercateo.jsonschema.property.collector;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MethodCollectorClasses {
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MethodAnnotation {
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

    static class StaticMethod {
        static Integer getNumber() {
            return 0;
        }

        String getString() {
            return "foo";
        }
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
