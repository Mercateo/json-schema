package com.mercateo.jsonschema.generictype;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

import com.googlecode.gentyref.GenericTypeReflector;

public final class GenericParameterizedType<T> extends GenericTypeAbstract<T, ParameterizedType> {

    GenericParameterizedType(ParameterizedType type, Class<T> rawType) {
        super(rawType, type);
    }

    @Override
    public String getSimpleName() {
        return rawType.getSimpleName();
    }

    @Override
    public GenericType<?> getContainedType() {
        Type[] actualTypeArguments = type.getActualTypeArguments();
        if (actualTypeArguments.length > 1) {
            throw new IllegalStateException(type + " not supported for subtyping");
        }
        return GenericType.of(actualTypeArguments[0], rawType);
    }

    @Override
    public GenericType<? super T> getSuperType() {
        final Class<? super T> superclass = rawType.getSuperclass();
        return superclass != null ? GenericType.of(GenericTypeReflector.getExactSuperType(type,
                superclass), superclass) : null;
    }

    @Override
    public String toString() {
        return type.toString();
    }

}
