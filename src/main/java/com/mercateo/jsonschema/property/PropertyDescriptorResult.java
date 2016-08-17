package com.mercateo.jsonschema.property;

import com.mercateo.immutables.Tuple;
import com.mercateo.jsonschema.generictype.GenericType;
import com.sun.javaws.jnl.PropertyDesc;
import javaslang.collection.Map;
import org.immutables.value.Value;

@Value.Immutable
@Tuple
interface PropertyDescriptorResult {
    PropertyDescriptor propertyDescriptor();

    Map<GenericType<?>, PropertyDescriptor> addedDescriptors();
}
