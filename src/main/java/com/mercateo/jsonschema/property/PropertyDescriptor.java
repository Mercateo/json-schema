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
public interface PropertyDescriptor {
    GenericType<?, ?> genericType();

    List<Property> children();

    Map<Class<? extends Annotation>, Set<Annotation>> annotations();
}
