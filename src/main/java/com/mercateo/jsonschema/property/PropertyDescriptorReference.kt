package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType

data class PropertyDescriptorReference(
        override val propertyType: PropertyType,
        override val genericType: GenericType<*>,
        override val annotations: Map<Class<out Annotation>, Set<Annotation>>
)
: PropertyDescriptor {
    private var internalChildren: List<Property>? = null

    internal var reference: String? = null

    override var children: List<Property>
        get() =
        internalChildren ?: emptyList()
        set(value) {
            internalChildren = value
        }
}
