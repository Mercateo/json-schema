package com.mercateo.jsonschema.property;

import java.lang.annotation.Annotation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

class AnnotationMapBuilder {
    Multimap<Class<? extends Annotation>, Annotation> createMap(
            Annotation[] annotations) {
        final Multimap<Class<? extends Annotation>, Annotation> annotationMap = HashMultimap
                .create();
        for (Annotation annotation : annotations) {
            annotationMap.put(annotation.annotationType(), annotation);
        }
        return annotationMap;
    }

    Multimap<Class<? extends Annotation>, Annotation> merge(
            Multimap<Class<? extends Annotation>, Annotation> annotations,
            Multimap<Class<? extends Annotation>, Annotation> otherAnnotations) {

        final HashMultimap<Class<? extends Annotation>, Annotation> mergedAnnotations = HashMultimap.create(
                annotations);
        mergedAnnotations.putAll(otherAnnotations);
        return mergedAnnotations;
    }
}
