package com.mercateo.jsonschema.generictype;

import com.googlecode.gentyref.GenericTypeReflector;

import java.lang.reflect.*;

import static java.util.Objects.requireNonNull;

public interface GenericType<T> {
    Class<T> getRawType();

    String getSimpleName();

    Type getType();

    GenericType<?> getContainedType();

    boolean isInstanceOf(Class<?> clazz);

    boolean isIterable();

    Field[] getDeclaredFields();

    GenericType<? super T> getSuperType();

    static GenericType<?> of(Type type) {
        return of(type, null);
    }

    @SuppressWarnings("unchecked")
    static <T> GenericType<T> of(Type type, Class<T> rawType) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return new GenericParameterizedType<>(parameterizedType, (Class<T>) parameterizedType
                .getRawType());
        } else if (type instanceof Class) {
            return new GenericClass<>((Class<T>) type);
        } else if (type instanceof GenericArrayType) {
            return new GenericArray<>((GenericArrayType) type, requireNonNull(rawType));
        }
        {
            throw new IllegalStateException("unhandled type " + type);
        }
    }

    static GenericType<?> of(Field field, Type type) {
        final Class<?> fieldClass = field.getType();
        final Type fieldType = GenericTypeReflector.getExactFieldType(field, type);
        return of(fieldType, fieldClass);
    }

    static GenericType<?> of(Method method, Type type) {
        final Class<?> returnClass = method.getReturnType();
        final Type returnType = GenericTypeReflector.getExactReturnType(method, type);
        return of(returnType, returnClass);
    }

}
