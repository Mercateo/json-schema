package com.mercateo.jsonschema.property;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.mercateo.jsonschema.generictype.GenericType;
import javaslang.collection.HashMap;
import javaslang.collection.HashSet;
import javaslang.collection.List;
import javaslang.collection.Set;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UnwrappedPropertyBuilder {

    private final PropertyBuilder propertyBuilder;

    private final Set<Class<? extends Annotation>> unwrapAnnotations;

    private ConcurrentHashMap<GenericType, Property> unwrappedProperties;

    private ConcurrentHashMap<GenericType, PropertyDescriptor> unwrappedPropertyDescriptors;

    public UnwrappedPropertyBuilder(PropertyBuilder propertyBuilder, Class<? extends Annotation>... unwrapAnnotations) {
        this.propertyBuilder = propertyBuilder;
        this.unwrapAnnotations = HashSet.of(unwrapAnnotations);

        this.unwrappedProperties = new ConcurrentHashMap<>();
        this.unwrappedPropertyDescriptors = new ConcurrentHashMap<>();
    }

    public Property from(Class<?> clazz) {
        return from(GenericType.of(clazz));
    }

    public Property from(GenericType<?> genericType) {
        return unwrappedProperties.computeIfAbsent(genericType, this::unwrap);
    }

    private Property unwrap(GenericType<?> genericType) {
        return unwrap(propertyBuilder.from(genericType));
    }

    private Property unwrap(Property property) {
        final PropertyDescriptor propertyDescriptor = unwrappedPropertyDescriptors.computeIfAbsent(
                property.genericType(), t -> createUnwrappedDescriptor(property));

        return ImmutableProperty.of(property.name(), propertyDescriptor, property.valueAccessor(),
                property.annotations());
    }

    private PropertyDescriptor createUnwrappedDescriptor(Property property) {
        List<Property> children = addChildren(List.empty(), property);
        return ImmutablePropertyDescriptor.of(property.genericType(), children, property
                .propertyDescriptor().annotations());
    }

    private List<Property> addChildren(List<Property> children, Property property) {
        return property.children()
                .map(this::unwrap)
                .flatMap(child -> {
                    if (!unwrapAnnotations.intersect(child.annotations().keySet()).isEmpty()) {
                        child = updateValueAccessors(child);
                        return addChildren(children, child);
                    } else {
                        return children.append(child);
                    }

        });
    }

    private Property updateValueAccessors(Property child) {
        final List<Property> children = child.children().map(c -> (Property) ImmutableProperty.of(c.name(), c.propertyDescriptor(),
                object -> {
                    Object intermediateObject = child.valueAccessor().apply(object);
                    return intermediateObject != null ? c.valueAccessor().apply(intermediateObject) : null;
                }, c.annotations())).toList();

        final PropertyDescriptor propertyDescriptor = child.propertyDescriptor();
        final PropertyDescriptor updatedPropertyDescriptor = ImmutablePropertyDescriptor.of(
                propertyDescriptor.genericType(),
                children,
                propertyDescriptor.annotations());
        return ImmutableProperty.of(child.name(), updatedPropertyDescriptor, child.valueAccessor(), child.annotations());
    }

}
