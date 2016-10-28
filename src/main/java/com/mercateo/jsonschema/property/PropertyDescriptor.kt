package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType

interface PropertyDescriptor {
    val propertyType: PropertyType

    val genericType: GenericType<*>

    val children: List<Property>

    val annotations: Map<Class<out Annotation>, Set<Annotation>>
}
