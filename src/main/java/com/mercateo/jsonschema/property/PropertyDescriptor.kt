package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType
import sun.font.TrueTypeFont

data class PropertyDescriptor(
        val propertyType: PropertyType,
        val genericType: GenericType<*>,
        val context: Context,
        val annotations: Map<Class<out Annotation>, Set<Annotation>>
) {
    val children: List<Property> =
            when (context) {
                is Context.Children -> context.children
                else -> emptyList()
            }

    val innerReference: Boolean =
            when (context) {
                is Context.InnerReference -> true
                else -> false
            }

    sealed class Context {
        class Children(val children: List<Property>) : Context()
        object InnerReference : Context()
    }
}

