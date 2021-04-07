package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType

data class RawProperty<S, T>(
    val name: String,
    val genericType: GenericType<T>,
    val annotations: Map<Class<out Annotation>, Set<Annotation>>,
    val valueAccessor: (S) -> T?
)
