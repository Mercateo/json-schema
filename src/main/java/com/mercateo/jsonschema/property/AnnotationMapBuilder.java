package com.mercateo.jsonschema.property;

import javaslang.collection.HashSet;
import javaslang.collection.Map;
import javaslang.collection.Set;
import javaslang.collection.Stream;

import java.lang.annotation.Annotation;

class AnnotationMapBuilder {
    Map<Class<? extends Annotation>, Set<Annotation>> createMap(
            Annotation[] annotations) {

        return Stream.of(annotations)
                .<Class<? extends Annotation>>groupBy(Annotation::annotationType)
                .mapValues(HashSet::ofAll);
    }

    Map<Class<? extends Annotation>, Set<Annotation>> merge(
            Map<Class<? extends Annotation>, Set<Annotation>> annotations,
            Map<Class<? extends Annotation>, Set<Annotation>> otherAnnotations) {

        return annotations.merge(otherAnnotations, Set::addAll);
    }
}
