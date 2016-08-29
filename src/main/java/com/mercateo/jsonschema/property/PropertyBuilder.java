package com.mercateo.jsonschema.property;

import com.mercateo.jsonschema.generictype.GenericType;
import com.mercateo.jsonschema.generictype.GenericTypeHierarchy;
import javaslang.collection.HashMap;
import javaslang.collection.HashSet;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Set;

import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class PropertyBuilder {
    private static final String ROOT_NAME = "#";

    private final GenericTypeHierarchy genericTypeHierarchy;

    private final List<RawPropertyCollector> rawPropertyCollectors;

    private final AnnotationMapBuilder annotationMapBuilder;

    private ConcurrentHashMap<GenericType<?, ?>, PropertyDescriptor> knownDescriptors;

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

    public Property from(GenericType<?, ?> genericType) {
        return from(ROOT_NAME, genericType, HashMap.empty(),
                PropertyBuilder::rootValueAccessor, HashSet.empty());
    }

    private Property from(String name, GenericType<?, ?> genericType,
                          Map<Class<? extends Annotation>, Set<Annotation>> annotations, Function valueAccessor, Set<GenericType<?, ?>> nestedTypes) {
        java.util.Map addedDescriptors = new java.util.HashMap<>();
        final Property property = from(name, genericType, annotations, valueAccessor, addedDescriptors, nestedTypes);
        knownDescriptors.putAll(addedDescriptors);
        return property;
    }

    private Property from(String name, GenericType<?, ?> genericType, Map<Class<? extends Annotation>, Set<Annotation>> annotations, Function valueAccessor,
                          java.util.Map<GenericType<?, ?>, PropertyDescriptor> addedDescriptors, Set<GenericType<?, ?>> nestedTypes) {
        final PropertyDescriptor propertyDescriptor = getPropertyDescriptor(genericType, addedDescriptors, nestedTypes);

        return ImmutableProperty.of(name, propertyDescriptor, valueAccessor, annotationMapBuilder
                .merge(annotations, propertyDescriptor.annotations()));
    }

    private PropertyDescriptor getPropertyDescriptor(GenericType<?, ?> genericType, java.util.Map<GenericType<?, ?>, PropertyDescriptor> addedDescriptors, Set<GenericType<?, ?>> nestedTypes) {
        if (knownDescriptors.containsKey(genericType)) {
            return knownDescriptors.get(genericType);
        } else {
            if (addedDescriptors.containsKey(genericType)) {
                return addedDescriptors.get(genericType);
            } else {
                return createPropertyDescriptor(genericType, addedDescriptors, nestedTypes);
            }
        }
    }

    private PropertyDescriptor createPropertyDescriptor(GenericType<?, ?> genericType, java.util.Map<GenericType<?, ?>, PropertyDescriptor> addedDescriptors, Set<GenericType<?, ?>> nestedTypes) {
        final PropertyType propertyType = PropertyTypeMapper.of(genericType);

        final List<Property> children;
        switch (propertyType) {
            case OBJECT:
                if (!nestedTypes.contains(genericType)) {
                    children = createChildProperties(genericType, nestedTypes.add(genericType));
                } else {
                    children = List.empty();
                }
                break;

            case ARRAY:
                final Property property = from("", genericType.getContainedType(), HashMap.empty(), o -> null, addedDescriptors, nestedTypes);
                children = List.of(property);
                break;

            default:
                children = List.empty();
                break;
        }

        final PropertyDescriptor propertyDescriptor = ImmutablePropertyDescriptor.of(genericType, children, annotationMapBuilder.createMap(
                genericType.getRawType().getAnnotations()));
        addedDescriptors.put(genericType, propertyDescriptor);
        return propertyDescriptor;
    }

    private List<Property> createChildProperties(GenericType<?, ?> genericType, Set<GenericType<?, ?>> nestedTypes) {
        return rawPropertyCollectors.flatMap(collector -> genericTypeHierarchy.hierarchy(
                genericType).flatMap(collector::forType)).map(rawProperty -> mapProperty(nestedTypes, rawProperty));
    }

    private Property mapProperty(Set<GenericType<?, ?>> nestedTypes, RawProperty rawProperty) {
        return from(rawProperty.name(), rawProperty.genericType(), rawProperty.annotations(),
                rawProperty.valueAccessor(), nestedTypes);
    }


}
