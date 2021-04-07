package com.mercateo.jsonschema.property.collector

import com.mercateo.jsonschema.generictype.GenericType
import com.mercateo.jsonschema.property.RawProperty
import com.mercateo.jsonschema.property.RawPropertyCollector
import com.mercateo.jsonschema.property.annotation.AnnotationProcessor
import java.lang.reflect.Field
import java.lang.reflect.Modifier

class FieldCollector(
    private val config: FieldCollectorConfig = FieldCollectorConfig(),
    private val annotationProcessor: AnnotationProcessor = AnnotationProcessor()
) : RawPropertyCollector {

    override fun <S> forType(genericType: GenericType<S>): Sequence<RawProperty<S, *>> {
        return sequenceOf(*genericType.declaredFields)
            .filter { !it.isSynthetic }
            .filter { !Modifier.isStatic(it.modifiers) }
            .filter { config.includePrivateFields || Modifier.isPublic(it.modifiers) }
            .map { mapRawDataProperty(it, genericType) }
    }

    private fun <S> mapRawDataProperty(field: Field, genericType: GenericType<S>): RawProperty<S, Any> {
        val fieldType = GenericType.ofField(field, genericType.type)

        return RawProperty(
            field.name,
            fieldType,
            annotationProcessor.collectAndGroup(field.annotations.toList())
        ) { instance: S -> valueAccessor(field, instance) }
    }

    private fun <S> valueAccessor(field: Field, instance: S): Any? {
        if (config.includePrivateFields) {
            field.isAccessible = true
        }
        return field.get(instance)
    }
}
