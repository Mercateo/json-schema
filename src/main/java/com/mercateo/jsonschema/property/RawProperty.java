package com.mercateo.jsonschema.property;

import com.mercateo.immutables.Tuple;
import com.mercateo.jsonschema.generictype.GenericType;
import javaslang.collection.Map;
import javaslang.collection.Set;
import org.immutables.value.Value;

import java.lang.annotation.Annotation;
import java.util.function.Function;

@Value.Immutable
@Tuple
public interface RawProperty {
    String name();

    GenericType<?> genericType();

    Map<Class<? extends Annotation>, Set<Annotation>> annotations();

    Function valueAccessor();
}
