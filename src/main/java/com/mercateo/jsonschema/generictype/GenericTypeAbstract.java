package com.mercateo.jsonschema.generictype;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

abstract class GenericTypeAbstract<T, U extends Type> implements GenericType<T> {

    protected final Class<T> rawType;

    protected final U type;

    GenericTypeAbstract(Class<T> rawType, U type) {
        this.rawType = requireNonNull(rawType);
        this.type = type;
    }

    @Override
    public final Class<T> getRawType() {
        return rawType;
    }

    @Override
    public U getType() {
        return type;
    }

    @Override
    public boolean isInstanceOf(Class<?> clazz) {
        return requireNonNull(clazz).isAssignableFrom(getRawType());
    }

    @Override
    public boolean isIterable() {
        return Iterable.class.isAssignableFrom(getRawType());
    }

    @Override
    public Field[] getDeclaredFields() {
        return getRawType().getDeclaredFields();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !GenericType.class.isAssignableFrom(o.getClass())) {
            return false;
        }
        if (getClass() != o.getClass()) return false;
        GenericType<?> that = (GenericType<?>) o;
        return Objects.equals(rawType, that.getRawType()) &&
            Objects.equals(type, that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, type);
    }
}
