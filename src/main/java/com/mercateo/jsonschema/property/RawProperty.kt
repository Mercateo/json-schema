package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType

data class RawProperty(
        val name: String,
        val genericType: GenericType<*>,
        val annotations: Map<Class<out Annotation>, Set<Annotation>>,
        val valueAccessor: (Any) -> Any?) {
}
