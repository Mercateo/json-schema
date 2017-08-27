package com.mercateo.jsonschema.property;

public class MethodCollectorClasses {
    static class StaticMethod {
        String getString() {
            return "foo";
        }

        static Integer getNumber() {
            return 0;
        }
    }
}
