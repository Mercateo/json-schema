package com.mercateo.jsonschema.property;

import com.google.common.collect.Multimap;
import com.mercateo.immutables.Tuple;
import com.mercateo.jsonschema.generictype.GenericType;
import org.immutables.value.Value;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

@Value.Immutable
@Tuple
public interface Property {
    String name();

    PropertyDescriptor propertyDescriptor();

    Function valueAccessor();

    default Object getValue(Object object) {
       return valueAccessor().apply(object);
    }

    default Collection<Property> children() {
        return propertyDescriptor().children();
    }

    default GenericType<?> genericType() {
        return propertyDescriptor().genericType();
    }

    Multimap<Class<? extends Annotation>, Annotation> annotations();
}
