package com.mercateo.jsonschema.schema;

import com.mercateo.immutables.Tuple;
import com.mercateo.jsonschema.property.Property;
import org.immutables.value.Value;

import static java.util.Objects.requireNonNull;

@Value.Immutable
@Tuple
public interface SchemaPropertyContext {

    PropertyChecker propertyChecker();

    default boolean isFieldApplicable(Property field) {
        return propertyChecker().test(requireNonNull(field));
    }
}