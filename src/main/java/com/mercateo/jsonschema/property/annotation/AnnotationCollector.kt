package com.mercateo.jsonschema.property.annotation

import java.util.*

class AnnotationCollector {
    fun collect(annotations: Iterable<Annotation>): List<Annotation> {
        val collectedAnnotations: MutableList<Annotation> = mutableListOf()

        val stack: Stack<Annotation> = Stack()
        annotations.forEach { stack.push(it) }

        while (stack.isNotEmpty()) {
            val annotation = stack.pop()
            collectedAnnotations.add(annotation)

            annotation.annotationClass.annotations
                    .filter(this::isNotInternalAnnotation)
                    .forEach { stack.push(it) }
        }

        return collectedAnnotations
    }

    private fun isNotInternalAnnotation(annotation: Annotation): Boolean {
        val packageName = annotation.annotationClass.java.`package`.name
        return !packageName.startsWith("java.lang.")
    }
}
