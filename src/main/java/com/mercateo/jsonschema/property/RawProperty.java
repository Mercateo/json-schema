package com.mercateo.jsonschema.property;

import com.google.common.collect.Multimap;
import com.mercateo.immutables.Tuple;
import com.mercateo.jsonschema.generictype.GenericType;
import org.immutables.value.Value;

import java.lang.annotation.Annotation;
import java.util.function.Function;

@Value.Immutable
@Tuple
public interface RawProperty {
    String name();

    GenericType<?> genericType();

    Multimap<Class<? extends Annotation>, Annotation> annotations();

    Function valueAccessor();
}
