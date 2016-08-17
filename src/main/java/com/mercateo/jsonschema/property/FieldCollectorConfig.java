package com.mercateo.jsonschema.property;

import com.mercateo.immutables.DataClass;
import org.immutables.value.Value;

@Value.Immutable
@DataClass
public interface FieldCollectorConfig {

    static FieldCollectorConfigBuilder builder() {
        return new FieldCollectorConfigBuilder();
    }

    @Value.Default
    default boolean includePrivateFields() {
        return true;
    }
}
