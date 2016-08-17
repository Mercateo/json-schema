package com.mercateo.jsonschema.property;

import java.lang.annotation.Annotation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import javaslang.collection.HashSet;
import javaslang.collection.Map;
import javaslang.collection.Set;
import javaslang.collection.Stream;

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
