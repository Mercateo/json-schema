package com.mercateo.jsonschema.schema;

import com.mercateo.immutables.Tuple;
import com.mercateo.jsonschema.property.Property;
import com.mercateo.jsonschema.property.RawPropertyCollector;
import javaslang.collection.List;
import org.immutables.value.Value;

import java.lang.annotation.Annotation;

import static java.util.Objects.requireNonNull;

@Value.Immutable
@Tuple
public interface SchemaPropertyContext {

    PropertyChecker propertyChecker();

    Annotation[] unwrapAnnotations();

    List<RawPropertyCollector> propertyCollectors();

    default boolean isApplicable(Property property) {
        return propertyChecker().test(requireNonNull(property));
    }
}