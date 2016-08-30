package com.mercateo.jsonschema.property;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.mercateo.jsonschema.generictype.GenericType;
import javaslang.collection.Stream;

public final class MethodCollector implements RawPropertyCollector {

    private final AnnotationMapBuilder annotationMapBuilder;

    public MethodCollector() {
        this.annotationMapBuilder = new AnnotationMapBuilder();
    }

    @Override
    public Stream<RawProperty> forType(GenericType<?> genericType) {
        return Stream.of(genericType.getRawType().getDeclaredMethods())
                .filter(method -> !method.isSynthetic())
                .filter(method -> method.getDeclaringClass() != Object.class)
                .filter( method -> method.getReturnType() != void.class)
                .filter(method -> method .getParameterCount() == 0)
                .map(method -> mapRawDataProperty(method, genericType.getType()));
    }

    private RawProperty mapRawDataProperty(Method method, Type type) {
        final String methodName = getPropertyName(method);
        return ImmutableRawProperty.of(methodName, GenericType.of(method, type),
                annotationMapBuilder.createMap(method.getAnnotations()), (
                        Object object) -> valueAccessor(method, object));
    }

    private String getPropertyName(Method method) {
        final String methodName = method.getName();
        if (methodName.startsWith("get") && Character.isUpperCase(methodName.charAt(3))) {
            return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
        }
        if (methodName.startsWith("is") && Character.isUpperCase(methodName.charAt(2))) {
            return Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
        }
        return methodName;
    }

    private Object valueAccessor(Method method, Object object) {
        try {
            return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}
