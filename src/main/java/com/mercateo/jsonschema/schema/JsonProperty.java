package com.mercateo.jsonschema.schema;

import com.mercateo.immutables.DataClass;
import com.mercateo.jsonschema.property.PropertyType;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@DataClass
public abstract class JsonProperty {

    public abstract String getId();

    public abstract String getName();

    public abstract PropertyType getType();

    public abstract String getRef();

    public abstract String getDefaultValue();

    @Value.Default
    public boolean isRequired() {
        return false;
    }

    public abstract List<JsonProperty> getProperties();

    public abstract SizeConstraints getSizeConstraints();

    public abstract ValueConstraints getValueConstraints();

    public abstract List<String> getAllowedValues();

    public abstract String getPath();
}

