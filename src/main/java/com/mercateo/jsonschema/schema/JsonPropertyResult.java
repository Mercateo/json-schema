package com.mercateo.jsonschema.schema;

import com.mercateo.immutables.Tuple;
import org.immutables.value.Value;

import java.util.Set;

@Value.Immutable
@Tuple
public interface JsonPropertyResult {
    JsonProperty getRoot();

    Set<JsonProperty> getReferencedElements();
}