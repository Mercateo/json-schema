package com.mercateo.jsonschema.generictype;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Objects;

import com.googlecode.gentyref.GenericTypeReflector;

final class GenericArray<T> extends GenericTypeAbstract<T, GenericArrayType> {

    GenericArray(GenericArrayType arrayType, Class<T> rawType) {
        super(rawType, arrayType);
    }

    @Override
    public String getSimpleName() {
        return type.getTypeName();
    }

    @Override
    public GenericType<?> getContainedType() {
        return GenericType.of(GenericTypeReflector.getArrayComponentType(type), getRawType()
                .getComponentType());
    }

    @Override
    public GenericType<? super T> getSuperType() {
        return null;
    }

    @Override
    public boolean isIterable() {
        return true;
    }
}
