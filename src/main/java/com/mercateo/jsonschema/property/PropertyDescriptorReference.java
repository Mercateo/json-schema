package com.mercateo.jsonschema.property;

import com.mercateo.immutables.Tuple;
import com.mercateo.jsonschema.generictype.GenericType;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Set;
import javaslang.control.Option;
import org.immutables.value.Value;

import java.lang.annotation.Annotation;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

@Value.Immutable
@Tuple
public abstract class PropertyDescriptorReference implements PropertyDescriptor {
    private Option<List<Property>> children;

    private String reference;

    public abstract PropertyType propertyType();

    public abstract GenericType<?> genericType();

    public List<Property> children() {
        return children.getOrElse(List.empty());
    }

    public abstract Map<Class<? extends Annotation>, Set<Annotation>> annotations();

    public void setChildren(List<Property> children) {
        checkState(children.nonEmpty(), "cannot set children twice");
        this.children = Option.of(children);
    }

    public String reference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
