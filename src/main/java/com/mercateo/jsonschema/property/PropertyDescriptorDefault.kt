package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType

data class PropertyDescriptorDefault<T>(
        override val propertyType: PropertyType,
        override val genericType: GenericType<T>,
        override val context: PropertyDescriptor.Context,
        override val annotations: Map<Class<out Annotation>, Set<Annotation>>
) : PropertyDescriptor<T> {
    override val children: List<Property<T, Any>> =
            when (context) {
                is PropertyDescriptor.Context.Children<*> -> context.children as List<Property<T, Any>>
                else -> emptyList()
            }

    val innerReference: Boolean =
            when (context) {
                is PropertyDescriptor.Context.InnerReference -> true
                else -> false
            }
}

