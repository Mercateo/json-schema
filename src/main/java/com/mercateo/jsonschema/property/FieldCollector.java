package com.mercateo.jsonschema.property;

import com.mercateo.jsonschema.generictype.GenericType;
import javaslang.collection.Stream;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public final class FieldCollector implements RawPropertyCollector {

    private final FieldCollectorConfig config;

    private final AnnotationMapBuilder annotationMapBuilder;

    public FieldCollector(FieldCollectorConfig config) {
        this.config = config;
        annotationMapBuilder = new AnnotationMapBuilder();
    }

    @Override
    public Stream<RawProperty> forType(GenericType<?, ?> genericType) {
        return Stream.of(genericType.getDeclaredFields())
                .filter(field -> !field.isSynthetic())
                .filter(field -> config.includePrivateFields() || Modifier.isPublic(field.getModifiers()))
                .map(field -> mapRawDataProperty(field, genericType.getType()));
    }

    private RawProperty mapRawDataProperty(Field field, Type type) {
        return ImmutableRawProperty.of(field.getName(), GenericType.of(field, type), annotationMapBuilder.createMap(field.getAnnotations()), (Object object) -> valueAccessor(field, object));
    }

    private Object valueAccessor(Field field, Object object) {
        try {
            if (config.includePrivateFields()) {
                field.setAccessible(true);
            }
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
