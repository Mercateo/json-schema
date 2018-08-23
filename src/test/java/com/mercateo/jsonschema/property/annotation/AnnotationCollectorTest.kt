package com.mercateo.jsonschema.property.annotation

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class AnnotationCollectorTest {

    private lateinit var annotationCollector: AnnotationCollector

    @Before
    fun setUp() {
        annotationCollector = AnnotationCollector()
    }

    @Test
    fun returnsAnnotations() {
        val annotations = AnnotationCollectorClasses.PropertyHolder::class.java.getDeclaredField("foo").annotations.toList()

        val result = annotationCollector.collect(annotations)

        assertThat(result).hasSize(1)
    }

    @Test
    fun returnsNestedAnnotations() {
        val annotations = AnnotationCollectorClasses.PropertyHolder::class.java.getDeclaredField("bar").annotations.toList()

        val result = annotationCollector.collect(annotations)

        assertThat(result).hasSize(2)
    }
}