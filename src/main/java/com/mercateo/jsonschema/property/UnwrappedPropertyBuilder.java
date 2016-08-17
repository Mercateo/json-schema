package com.mercateo.jsonschema.property;

import com.mercateo.jsonschema.generictype.GenericType;
import javaslang.collection.HashSet;
import javaslang.collection.List;
import javaslang.collection.Set;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        java.util.Map addedUnwrappedPropertyDescriptors = new java.util.HashMap<>();
        final Property property = unwrap(propertyBuilder.from(genericType), addedUnwrappedPropertyDescriptors);
        unwrappedPropertyDescriptors.putAll(addedUnwrappedPropertyDescriptors);
        return property;
    }

    private Property unwrap(Property property, java.util.Map<GenericType<?>, PropertyDescriptor> addedUnwrappedProperties) {
        final GenericType<?> genericType = property.genericType();

        final PropertyDescriptor propertyDescriptor;
        if (unwrappedPropertyDescriptors.containsKey(genericType)) {
            propertyDescriptor = unwrappedPropertyDescriptors.get(genericType);
        } else {
            if (addedUnwrappedProperties.containsKey(genericType)){
                propertyDescriptor = addedUnwrappedProperties.get(genericType);
            } else {
                propertyDescriptor = createUnwrappedDescriptor(property, addedUnwrappedProperties);
            }
        }
        return ImmutableProperty.of(property.name(), propertyDescriptor, property.valueAccessor(),
                property.annotations());
    }

    private PropertyDescriptor createUnwrappedDescriptor(Property property, Map<GenericType<?>, PropertyDescriptor> addedUnwrappedProperties) {
        List<Property> children = addChildren(List.empty(), property, addedUnwrappedProperties);
        final GenericType<?> genericType = property.genericType();
        final PropertyDescriptor propertyDescriptor = ImmutablePropertyDescriptor.of(genericType, children, property
                .propertyDescriptor().annotations());
        addedUnwrappedProperties.put(genericType, propertyDescriptor);
        return propertyDescriptor;
    }

    private List<Property> addChildren(List<Property> children, Property property, Map<GenericType<?>, PropertyDescriptor> addedUnwrappedProperties) {
        return property.children()
                .map(child -> unwrap(child, addedUnwrappedProperties))
                .flatMap(child -> {
                    if (!unwrapAnnotations.intersect(child.annotations().keySet()).isEmpty()) {
                        child = updateValueAccessors(child);
                        return addChildren(children, child, addedUnwrappedProperties);
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
