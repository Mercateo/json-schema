package com.mercateo.jsonschema.property;

import com.mercateo.immutables.DataClass;
import com.mercateo.jsonschema.property.FieldCollectorConfigBuilder;
import org.immutables.value.Value;

@Value.Immutable
@DataClass
public interface FieldCollectorConfig {

    @Value.Default
    default boolean includePrivateFields() {
        return true;
    }

    static FieldCollectorConfigBuilder builder() {
        return new FieldCollectorConfigBuilder();
    }
}
