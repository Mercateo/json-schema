package com.mercateo.jsonschema.property.annotation

import java.util.*

class AnnotationMapBuilder {
    fun createMap(vararg annotations: Annotation): Map<Class<out Annotation>, Set<Annotation>> {
        return annotations.groupBy({ it -> it.annotationClass.java }, { it }).mapValues { it.value.toSet() }
    }

    fun merge(annotations: Map<Class<out Annotation>, Set<Annotation>>,
              otherAnnotations: Map<Class<out Annotation>, Set<Annotation>>): Map<Class<out Annotation>, Set<Annotation>> {

        return annotations.mergeReduce(otherAnnotations, { obj, elements -> obj + elements })
    }

    private fun <K, V> Map<K, V>.mergeReduce(other: Map<K, V>, reduce: (V, V) -> V): Map<K, V> {
        val result = HashMap<K, V>(this.size + other.size)
        result.putAll(this)
        other.forEach { e ->
            val existing = result[e.key]

            result[e.key] = if (existing == null) {
                e.value
            } else {
                reduce(e.value, existing)
            }
        }

        return result
    }
}
