package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.Type

class FieldCollector(
        private val config: FieldCollectorConfig,
        private val annotationMapBuilder: AnnotationMapBuilder = AnnotationMapBuilder()
) : RawPropertyCollector {

    override fun forType(genericType: GenericType<*>): Sequence<RawProperty> {
        return sequenceOf(*genericType.declaredFields)
                .filter { !it.isSynthetic }
                .filter { config.includePrivateFields || Modifier.isPublic(it.modifiers) }
                .map { mapRawDataProperty(it, genericType.type) }
    }

    private fun mapRawDataProperty(field: Field, type: Type): RawProperty {
        return RawProperty(field.name,
                GenericType.ofField(field, type),
                annotationMapBuilder.createMap(*field.annotations),
                { instance: Any -> valueAccessor(field, instance) })
    }

    private fun valueAccessor(field: Field, instance: Any): Any? {
        if (config.includePrivateFields) {
            field.isAccessible = true
        }
        return field.get(instance)
    }
}
