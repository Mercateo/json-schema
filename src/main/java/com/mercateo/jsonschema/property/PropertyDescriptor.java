package com.mercateo.jsonschema.property;

import com.google.common.collect.Multimap;
import com.mercateo.immutables.Tuple;
import com.mercateo.jsonschema.generictype.GenericType;
import org.immutables.value.Value;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Value.Immutable
@Tuple
public interface PropertyDescriptor {
    GenericType<?> genericType();

    Collection<Property> children();

    Multimap<Class<? extends Annotation>, Annotation> annotations();
}
