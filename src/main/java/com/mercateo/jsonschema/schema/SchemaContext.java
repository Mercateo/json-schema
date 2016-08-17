package com.mercateo.jsonschema.schema;

import com.mercateo.immutables.Tuple;
import org.immutables.value.Value;

@Value.Immutable
@Tuple
public interface SchemaContext {
    PropertyChecker propertyChecker();
}
