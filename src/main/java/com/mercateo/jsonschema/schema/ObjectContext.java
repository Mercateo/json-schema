package com.mercateo.jsonschema.schema;

import com.mercateo.immutables.DataClass;
import com.mercateo.immutables.Tuple;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@Tuple
public interface ObjectContext<T> {

    List<T> allowedValues();

    T defaultValue();
}
