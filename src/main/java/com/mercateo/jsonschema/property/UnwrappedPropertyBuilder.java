package com.mercateo.jsonschema.property;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.mercateo.jsonschema.generictype.GenericType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UnwrappedPropertyBuilder {

    private final PropertyBuilder propertyBuilder;

    private ConcurrentHashMap<GenericType, Property> unwrappedProperties;

    private ConcurrentHashMap<GenericType, PropertyDescriptor> unwrappedPropertyDescriptors;

    public UnwrappedPropertyBuilder(PropertyBuilder propertyBuilder) {
        this.propertyBuilder = propertyBuilder;

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
        List<Property> children = new ArrayList<>();
        addChildren(children, property);
        return ImmutablePropertyDescriptor.of(property.genericType(), children, property
                .propertyDescriptor().annotations());
    }

    private void addChildren(List<Property> children, Property property) {
        for (Property child : property.children()) {
            child = unwrap(child);
            if (child.annotations().containsKey(JsonUnwrapped.class)) {
                child = updateValueAccessors(child);
                addChildren(children, child);
            } else {
                children.add(child);
            }
        }
    }

    private Property updateValueAccessors(Property child) {
        final List<Property> children = child.children().stream().map(c -> ImmutableProperty.of(c.name(), c.propertyDescriptor(),
                object -> {
                    Object intermediateObject = child.valueAccessor().apply(object);
                    return intermediateObject != null ? c.valueAccessor().apply(intermediateObject) : null;
                }, c.annotations())).collect(Collectors.toList());

        final PropertyDescriptor propertyDescriptor = child.propertyDescriptor();
        final PropertyDescriptor updatedPropertyDescriptor = ImmutablePropertyDescriptor.of(
                propertyDescriptor.genericType(),
                children,
                propertyDescriptor.annotations());
        return ImmutableProperty.of(child.name(), updatedPropertyDescriptor, child.valueAccessor(), child.annotations());
    }

}
