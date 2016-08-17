package com.mercateo.jsonschema.property;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mercateo.jsonschema.property.PropertyType;
import com.mercateo.jsonschema.property.PropertyTypeMapper;
import com.mercateo.jsonschema.generictype.GenericType;
import com.mercateo.jsonschema.generictype.GenericTypeHierarchy;
import javaslang.collection.HashSet;
import javaslang.collection.Set;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PropertyBuilder {
    private static final String ROOT_NAME = "#";

    private final GenericTypeHierarchy genericTypeHierarchy;

    private final List<RawPropertyCollector> rawPropertyCollectors;

    private final AnnotationMapBuilder annotationMapBuilder;

    private ConcurrentHashMap<GenericType<?>, PropertyDescriptor> knownDescriptors;

    public PropertyBuilder(List<RawPropertyCollector> rawPropertyCollectors) {
        this.rawPropertyCollectors = rawPropertyCollectors;
        this.genericTypeHierarchy = new GenericTypeHierarchy();
        this.annotationMapBuilder = new AnnotationMapBuilder();
        this.knownDescriptors = new ConcurrentHashMap<>();
    }

    private static Object rootValueAccessor(Object object) {
        throw new IllegalStateException("cannot call value accessor for root element");
    }

    public Property from(Class<?> propertyClass) {
        return from(GenericType.of(propertyClass));
    }

    public Property from(GenericType<?> genericType) {
        return from(ROOT_NAME, genericType, HashMultimap.create(),
                PropertyBuilder::rootValueAccessor, HashSet.empty());
    }

    private Property from(String name, GenericType<?> genericType,
                         Multimap<Class<? extends Annotation>, Annotation> annotations, Function valueAccessor, Set<GenericType<?>> nestedTypes) {
        final Map<GenericType<?>, PropertyDescriptor> addedDescriptors = new HashMap<>();
        final Property property = from(name, genericType, annotations, valueAccessor, addedDescriptors, nestedTypes);
        knownDescriptors.putAll(addedDescriptors);
        return property;
    }

    private Property from(String name, GenericType<?> genericType, Multimap<Class<? extends Annotation>, Annotation> annotations, Function valueAccessor,
                          Map<GenericType<?>, PropertyDescriptor> addedDescriptors, Set<GenericType<?>> nestedTypes) {
        final PropertyDescriptor propertyDescriptor = getPropertyDescriptor(genericType, addedDescriptors, nestedTypes);

        return ImmutableProperty.of(name, propertyDescriptor, valueAccessor, annotationMapBuilder
                .merge(annotations, propertyDescriptor.annotations()));
    }

    private PropertyDescriptor getPropertyDescriptor(GenericType<?> genericType, Map<GenericType<?>, PropertyDescriptor> addedDescriptors, Set<GenericType<?>> nestedTypes) {
        if (knownDescriptors.containsKey(genericType)) {
            return knownDescriptors.get(genericType);
        } else {
            return addedDescriptors.computeIfAbsent(genericType, type -> createPropertyDescriptor(type, addedDescriptors, nestedTypes));
        }
    }

    private PropertyDescriptor createPropertyDescriptor(GenericType<?> genericType, Map<GenericType<?>, PropertyDescriptor> addedDescriptors, Set<GenericType<?>> nestedTypes) {
        final PropertyType propertyType = PropertyTypeMapper.of(genericType);

        final List<Property> children;
        switch (propertyType) {
            case OBJECT:
                if (!nestedTypes.contains(genericType)) {
                    children = createChildProperties(genericType, nestedTypes.add(genericType));
                } else {
                    children = Collections.emptyList();
                }
                break;

            case ARRAY:
                children = Collections.singletonList(from("", genericType.getContainedType(), HashMultimap.create(), o -> null, addedDescriptors, nestedTypes));
                break;

            default:
                children = Collections.emptyList();
                break;
        }

        return ImmutablePropertyDescriptor.of(genericType, children, annotationMapBuilder.createMap(
                genericType.getRawType().getAnnotations()));
    }

    private List<Property> createChildProperties(GenericType<?> genericType, Set<GenericType<?>> nestedTypes) {
        return rawPropertyCollectors.stream().flatMap(collector -> genericTypeHierarchy.hierarchy(
                genericType).flatMap(collector::forType)).map(rawProperty -> mapProperty(nestedTypes, rawProperty)).collect(Collectors
                .toList());
    }

        private Property mapProperty(Set<GenericType<?>> nestedTypes, RawProperty rawProperty) {
            return from(rawProperty.name(), rawProperty.genericType(), rawProperty.annotations(),
                    rawProperty.valueAccessor(), nestedTypes);
        }


}
