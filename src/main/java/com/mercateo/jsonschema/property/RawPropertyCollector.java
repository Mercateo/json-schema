package com.mercateo.jsonschema.property;

import java.util.stream.Stream;

import com.mercateo.jsonschema.generictype.GenericType;

public interface RawPropertyCollector {
    Stream<RawProperty> forType(GenericType<?> genericType);

    default Stream<RawProperty> forType(Class<?> clazz) {
        return forType(GenericType.of(clazz));
    }
}
