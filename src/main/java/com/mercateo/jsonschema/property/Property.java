package com.mercateo.jsonschema.property;

import com.mercateo.immutables.Tuple;
import com.mercateo.jsonschema.generictype.GenericType;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Set;
import org.immutables.value.Value;

import java.lang.annotation.Annotation;
import java.util.Collection;
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

    default List<Property> children() {
        return propertyDescriptor().children();
    }

    default GenericType<?, ?> genericType() {
        return propertyDescriptor().genericType();
    }

    Map<Class<? extends Annotation>, Set<Annotation>> annotations();
}
