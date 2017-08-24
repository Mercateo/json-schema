package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType
import java.lang.reflect.Field
import java.lang.reflect.Modifier

class FieldCollector(
        private val config: FieldCollectorConfig = FieldCollectorConfig(),
        private val annotationMapBuilder: AnnotationMapBuilder = AnnotationMapBuilder()
) : RawPropertyCollector {

    override fun <S> forType(genericType: GenericType<S>): Sequence<RawProperty<S, *>> {
        return sequenceOf(*genericType.declaredFields)
                .filter { !it.isSynthetic }
                .filter { config.includePrivateFields || Modifier.isPublic(it.modifiers) }
                .map { mapRawDataProperty(it, genericType) }
    }

    private fun <S> mapRawDataProperty(field: Field, genericType: GenericType<S>): RawProperty<S, Any> {
        val fieldType = GenericType.ofField(field, genericType.type)

        return RawProperty<S, Any>(field.name,
                fieldType,
                annotationMapBuilder.createMap(*field.annotations),
                { instance: S -> valueAccessor(field, instance)})
    }

    private fun <S> valueAccessor(field: Field, instance: S): Any? {
        if (config.includePrivateFields) {
            field.isAccessible = true
        }
        return field.get(instance)
    }
}
