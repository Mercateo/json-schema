package com.mercateo.jsonschema.generictype;

import com.googlecode.gentyref.GenericTypeReflector;

import java.lang.reflect.*;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public abstract class GenericType<T, U extends Type> {

    protected final Class<T> rawType;

    protected final U type;

    GenericType(Class<T> rawType, U type) {
        this.rawType = requireNonNull(rawType);
        this.type = type;
    }

    public static GenericType<?,?> of(Type type) {
        return of(type, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> GenericType<T,?> of(Type type, Class<T> rawType) {
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

    public static GenericType<?,?> of(Field field, Type type) {
        final Class<?> fieldClass = field.getType();
        final Type fieldType = GenericTypeReflector.getExactFieldType(field, type);
        return of(fieldType, fieldClass);
    }

    public static GenericType<?,?> of(Method method, Type type) {
        final Class<?> returnClass = method.getReturnType();
        final Type returnType = GenericTypeReflector.getExactReturnType(method, type);
        return of(returnType, returnClass);
    }

    public final Class<T> getRawType() {
        return rawType;
    }

    public abstract String getSimpleName();

    public U getType() {
        return type;
    }

    public abstract GenericType<?, ?> getContainedType();

    public boolean isInstanceOf(Class<?> clazz) {
        return requireNonNull(clazz).isAssignableFrom(getRawType());
    }

    public boolean isIterable() {
        return Iterable.class.isAssignableFrom(getRawType());
    }

    public Field[] getDeclaredFields() {
        return getRawType().getDeclaredFields();
    }

    public abstract GenericType<? super T, ?> getSuperType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !GenericType.class.isAssignableFrom(o.getClass())) {
            return false;
        }
        if (getClass() != o.getClass()) return false;
        GenericType<?, ?> that = (GenericType<?, ?>) o;
        return Objects.equals(rawType, that.rawType) &&
            Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, type);
    }
}
