package com.mercateo.jsonschema.property;

import java.util.stream.Stream;

import com.mercateo.jsonschema.generictype.GenericType;

public interface RawPropertyCollector {
    Stream<RawProperty> forType(GenericType<?> genericType);
}
