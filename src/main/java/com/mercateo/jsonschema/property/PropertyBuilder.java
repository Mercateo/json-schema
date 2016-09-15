package com.mercateo.jsonschema.property;

import com.mercateo.jsonschema.generictype.GenericType;

public interface PropertyBuilder {
    Property from(Class<?> propertyClass);

    Property from(GenericType<?> genericType);
}
