package com.mercateo.jsonschema.property;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mercateo.jsonschema.property.PropertyType;
import com.mercateo.jsonschema.property.PropertyTypeMapper;
import com.mercateo.jsonschema.generictype.GenericType;
import com.mercateo.jsonschema.generictype.GenericTypeHierarchy;
import javaslang.collection.HashMap;
import javaslang.collection.HashSet;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Set;
import javaslang.collection.Stream;

import java.lang.annotation.Annotation;
import java.util.Collections;
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
        return from(ROOT_NAME, genericType, HashMap.empty(),
                PropertyBuilder::rootValueAccessor, HashSet.empty());
    }

    private Property from(String name, GenericType<?> genericType,
                         Map<Class<? extends Annotation>, Set<Annotation>> annotations, Function valueAccessor, Set<GenericType<?>> nestedTypes) {
        final PropertyResult result = from(name, genericType, annotations, valueAccessor, HashMap.empty(), nestedTypes);
        knownDescriptors.putAll(result.addedDescriptors().toJavaMap());
        return result.property();
    }

    private PropertyResult from(String name, GenericType<?> genericType, Map<Class<? extends Annotation>, Set<Annotation>> annotations, Function valueAccessor,
                          Map<GenericType<?>, PropertyDescriptor> addedDescriptors, Set<GenericType<?>> nestedTypes) {
        final PropertyDescriptorResult result = getPropertyDescriptor(genericType, addedDescriptors, nestedTypes);

        final PropertyDescriptor propertyDescriptor = result.propertyDescriptor();
        return ImmutablePropertyResult.of(ImmutableProperty.of(name, propertyDescriptor, valueAccessor, annotationMapBuilder
                .merge(annotations, propertyDescriptor.annotations())), result.addedDescriptors());
    }

    private PropertyDescriptorResult getPropertyDescriptor(GenericType<?> genericType, Map<GenericType<?>, PropertyDescriptor> addedDescriptors, Set<GenericType<?>> nestedTypes) {
        if (knownDescriptors.containsKey(genericType)) {
            return ImmutablePropertyDescriptorResult.of(knownDescriptors.get(genericType), addedDescriptors);
        } else {
            if (addedDescriptors.containsKey(genericType)) {
                return ImmutablePropertyDescriptorResult.of(addedDescriptors.apply(genericType), addedDescriptors);
            } else {
                return createPropertyDescriptor(genericType, addedDescriptors, nestedTypes);
            }
        }
    }

    private PropertyDescriptorResult createPropertyDescriptor(GenericType<?> genericType, Map<GenericType<?>, PropertyDescriptor> addedDescriptors, Set<GenericType<?>> nestedTypes) {
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
                final PropertyResult propertyResult = from("", genericType.getContainedType(), HashMap.empty(), o -> null, addedDescriptors, nestedTypes);
                children = List.of(propertyResult.property());
                addedDescriptors = propertyResult.addedDescriptors();
                break;

            default:
                children = List.empty();
                break;
        }

        final ImmutablePropertyDescriptor propertyDescriptor = ImmutablePropertyDescriptor.of(genericType, children, annotationMapBuilder.createMap(
                genericType.getRawType().getAnnotations()));
        return ImmutablePropertyDescriptorResult.of(propertyDescriptor, addedDescriptors.put(genericType, propertyDescriptor));
    }

    private List<Property> createChildProperties(GenericType<?> genericType, Set<GenericType<?>> nestedTypes) {
        return rawPropertyCollectors.flatMap(collector -> genericTypeHierarchy.hierarchy(
                genericType).flatMap(collector::forType)).map(rawProperty -> mapProperty(nestedTypes, rawProperty));
    }

        private Property mapProperty(Set<GenericType<?>> nestedTypes, RawProperty rawProperty) {
            return from(rawProperty.name(), rawProperty.genericType(), rawProperty.annotations(),
                    rawProperty.valueAccessor(), nestedTypes);
        }


}
