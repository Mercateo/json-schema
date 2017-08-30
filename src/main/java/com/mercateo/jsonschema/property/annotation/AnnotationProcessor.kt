package com.mercateo.jsonschema.property.annotation

import java.util.*

class AnnotationProcessor(
        private val annotationCollector: AnnotationCollector = AnnotationCollector(),
        private val annotationMapBuilder: AnnotationMapBuilder = AnnotationMapBuilder()
) {
    fun collectAndGroup(vararg annotations: Annotation): Map<Class<out Annotation>, Set<Annotation>> {
        val allAnnotations = annotationCollector.collect(*annotations)
        return annotationMapBuilder.createMap(*allAnnotations)
    }
}
