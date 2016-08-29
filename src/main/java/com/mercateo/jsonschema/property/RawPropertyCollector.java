package com.mercateo.jsonschema.property;


import com.mercateo.jsonschema.generictype.GenericType;
import javaslang.collection.Stream;

public interface RawPropertyCollector {
    Stream<RawProperty> forType(GenericType<?, ?> genericType);

    default Stream<RawProperty> forType(Class<?> clazz) {
        return forType(GenericType.of(clazz));
    }
}
