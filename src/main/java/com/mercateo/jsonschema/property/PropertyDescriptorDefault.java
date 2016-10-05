package com.mercateo.jsonschema.property;

import com.mercateo.immutables.Tuple;
import com.mercateo.jsonschema.generictype.GenericType;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Set;
import org.immutables.value.Value;

import java.lang.annotation.Annotation;

@Value.Immutable
@Tuple
public abstract class PropertyDescriptorDefault implements PropertyDescriptor {
    public abstract PropertyType propertyType();

    public abstract GenericType<?> genericType();

    public abstract List<Property> children();

    public abstract Map<Class<? extends Annotation>, Set<Annotation>> annotations();
}
