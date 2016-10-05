package com.mercateo.jsonschema.property;

import com.mercateo.jsonschema.generictype.GenericType;
import com.mercateo.jsonschema.generictype.GenericTypeHierarchy;
import javaslang.collection.HashMap;
import javaslang.collection.HashSet;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Set;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class PropertyBuilderDefault implements PropertyBuilder {
    private static final String ROOT_NAME = "#";

    private final GenericTypeHierarchy genericTypeHierarchy;

    private final List<RawPropertyCollector> rawPropertyCollectors;

    private final AnnotationMapBuilder annotationMapBuilder;

    private ConcurrentHashMap<GenericType<?>, PropertyDescriptor> knownDescriptors;

    public PropertyBuilderDefault(List<RawPropertyCollector> rawPropertyCollectors) {
        this.rawPropertyCollectors = rawPropertyCollectors;
        this.genericTypeHierarchy = new GenericTypeHierarchy();
        this.annotationMapBuilder = new AnnotationMapBuilder();
        this.knownDescriptors = new ConcurrentHashMap<>();
    }

    private static Object rootValueAccessor(Object object) {
        throw new IllegalStateException("cannot call value accessor for root element");
    }

    @Override
    public Property from(Class<?> propertyClass) {
        return from(GenericType.of(propertyClass));
    }

    @Override
    public Property from(GenericType<?> genericType) {
        return from(ROOT_NAME, genericType, HashMap.empty(),
                PropertyBuilderDefault::rootValueAccessor, HashSet.empty());
    }

    private Property from(String name, GenericType<?> genericType,
                          Map<Class<? extends Annotation>, Set<Annotation>> annotations, Function valueAccessor, Set<GenericType<?>> nestedTypes) {
        java.util.Map<GenericType<?>, PropertyDescriptor> addedDescriptors = new java.util.HashMap<>();
        java.util.Map<GenericType<?>, java.util.List<PropertyDescriptorReference>> addedReferences = new java.util.HashMap<>();
        final Property property = from(name, genericType, annotations, valueAccessor, addedDescriptors, addedReferences, nestedTypes);
        knownDescriptors.putAll(addedDescriptors);

        for (java.util.Map.Entry<GenericType<?>, java.util.List<PropertyDescriptorReference>> entry : addedReferences.entrySet()) {
            final PropertyDescriptor propertyDescriptor = knownDescriptors.get(entry.getKey());
            entry.getValue().forEach(propertyDescriptorReference -> {
                propertyDescriptorReference.setChildren(propertyDescriptor.children());
                propertyDescriptorReference.setReference("todo");
            });
        }
        return property;
    }

    private Property from(String name, GenericType<?> genericType, Map<Class<? extends Annotation>, Set<Annotation>> annotations, Function valueAccessor,
                          java.util.Map<GenericType<?>, PropertyDescriptor> addedDescriptors, java.util.Map<GenericType<?>, java.util.List<PropertyDescriptorReference>> addedReferences, Set<GenericType<?>> nestedTypes) {
        final PropertyDescriptor propertyDescriptor = getPropertyDescriptor(genericType, addedDescriptors, addedReferences, nestedTypes);

        return ImmutableProperty.of(name, propertyDescriptor, valueAccessor, annotationMapBuilder
                .merge(annotations, propertyDescriptor.annotations()));
    }

    private PropertyDescriptor getPropertyDescriptor(GenericType<?> genericType, java.util.Map<GenericType<?>, PropertyDescriptor> addedDescriptors, java.util.Map<GenericType<?>, java.util.List<PropertyDescriptorReference>> addedReferences, Set<GenericType<?>> nestedTypes) {
        if (knownDescriptors.containsKey(genericType)) {
            return knownDescriptors.get(genericType);
        } else {
            if (addedDescriptors.containsKey(genericType)) {
                return addedDescriptors.get(genericType);
            } else {
                if (!nestedTypes.contains(genericType)) {
                    return createPropertyDescriptor(genericType, addedDescriptors, addedReferences, nestedTypes);
                } else {
                    final PropertyType propertyType = PropertyTypeMapper.of(genericType);
                    final PropertyDescriptorReference propertyDescriptorReference = ImmutablePropertyDescriptorReference.of(propertyType, genericType,
                            annotationMapBuilder.createMap(genericType.getRawType().getAnnotations()));
                    addedReferences.computeIfAbsent(genericType, ignore -> new ArrayList()).add(propertyDescriptorReference);
                    return propertyDescriptorReference;
                }
            }
        }
    }

    private PropertyDescriptor createPropertyDescriptor(GenericType<?> genericType, java.util.Map<GenericType<?>, PropertyDescriptor> addedDescriptors, java.util.Map<GenericType<?>, java.util.List<PropertyDescriptorReference>> addedReferences, Set<GenericType<?>> nestedTypes) {
        final PropertyType propertyType = PropertyTypeMapper.of(genericType);

        final List<Property> children;
        switch (propertyType) {
            case OBJECT:
                children = createChildProperties(genericType, addedDescriptors, addedReferences, nestedTypes.add(genericType));
                break;

            case ARRAY:
                final Property property = from("", genericType.getContainedType(), HashMap.empty(), o -> null, addedDescriptors, addedReferences, nestedTypes);
                children = List.of(property);
                break;

            default:
                children = List.empty();
                break;
        }

        final PropertyDescriptor propertyDescriptor = ImmutablePropertyDescriptorDefault.of(propertyType, genericType, children, annotationMapBuilder.createMap(
                genericType.getRawType().getAnnotations()));
        addedDescriptors.put(genericType, propertyDescriptor);
        return propertyDescriptor;
    }

    private List<Property> createChildProperties(GenericType<?> genericType, java.util.Map<GenericType<?>, PropertyDescriptor> addedDescriptors, java.util.Map<GenericType<?>, java.util.List<PropertyDescriptorReference>> addedReferences, Set<GenericType<?>> nestedTypes) {
        return rawPropertyCollectors.flatMap(collector -> genericTypeHierarchy.hierarchy(
                genericType).flatMap(collector::forType)).map(rawProperty -> mapProperty(rawProperty, addedDescriptors, addedReferences, nestedTypes));
    }

    private Property mapProperty(RawProperty rawProperty, java.util.Map<GenericType<?>, PropertyDescriptor> addedDescriptors, java.util.Map<GenericType<?>, java.util.List<PropertyDescriptorReference>> addedReferences, Set<GenericType<?>> nestedTypes) {
        return from(rawProperty.name(), rawProperty.genericType(), rawProperty.annotations(),
                rawProperty.valueAccessor(), addedDescriptors, addedReferences, nestedTypes);
    }


}
