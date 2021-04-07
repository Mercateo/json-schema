package com.mercateo.jsonschema.property.annotation

class AnnotationProcessor(
    private val annotationCollector: AnnotationCollector = AnnotationCollector(),
    private val annotationMapBuilder: AnnotationMapBuilder = AnnotationMapBuilder()
) {

    fun collectAndGroup(annotations: Iterable<Annotation>): Map<Class<out Annotation>, Set<Annotation>> {
        val allAnnotations = annotationCollector.collect(annotations)
        return annotationMapBuilder.createMap(allAnnotations)
    }
}
