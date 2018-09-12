package com.mercateo.jsonschema.property.annotation

import org.assertj.core.api.MapAssert
import org.junit.Before
import org.junit.Test
import kotlin.reflect.full.memberProperties

class AnnotationMapBuilderTest {

    private lateinit var annotationMapBuilder: AnnotationMapBuilder

    @Before
    fun setUp() {
        annotationMapBuilder = AnnotationMapBuilder()
    }

    @Test
    fun createMapShouldReturnEmptyMap() {
        val annotationMap = annotationMapBuilder.createMap(emptyList())

        assertThat(annotationMap).isEmpty()
    }

    @Retention(AnnotationRetention.RUNTIME)
    internal annotation class Test1

    @Retention(AnnotationRetention.RUNTIME)
    internal annotation class Test2

    @Retention(AnnotationRetention.RUNTIME)
    internal annotation class Test3

    internal class TestClass {
        @Test1
        val value: String? = null

        @Test2
        @Test3
        val number: Int? = null
    }

    @Test
    fun createMapShouldReturnSingleValueMap() {
        val annotations = TestClass::class.memberProperties
                .filter { it.name == "value" }.first().annotations
        val annotationMap = annotationMapBuilder.createMap(annotations)

        assertThat(annotationMap).hasSize(1)
    }

    @Test
    fun testMerge() {
        val annotations = TestClass::class.memberProperties.first().annotations
        val annotationMap = annotationMapBuilder.createMap(annotations)

        val annotations2 = TestClass::class.memberProperties.last().annotations
        val annotationMap2 = annotationMapBuilder.createMap(annotations2)

        val result = annotationMapBuilder.merge(annotationMap, annotationMap2)

        assertThat(result).hasSize(3)
    }

    fun <K, V> assertThat(actual: Map<K, V>) = MapAssert(actual)
}