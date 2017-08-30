package com.mercateo.jsonschema.property.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AnnotationCollectorClasses {
    @Retention(RetentionPolicy.RUNTIME)
    @interface Annotation1 {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Annotation1
    @interface Annotation2 {
    }

    static class PropertyHolder {
        @Annotation1
        public String foo;

        @Annotation2
        public String bar;
    }
}
