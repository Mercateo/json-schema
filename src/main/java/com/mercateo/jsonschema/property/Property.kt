package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType

data class Property(
        val name: String,
        val propertyDescriptor: PropertyDescriptor,
        val valueAccessor: (Any) -> Any?,
        val annotations: Map<Class<out Annotation>, Set<Annotation>>,
        val context: Context = Property.Context.Unconnected
) {
    fun getValue(instance: Any): Any? {
        return valueAccessor(instance)
    }

    val children: List<Property> = propertyDescriptor.children

    val reference: String? = when (context) {
        is Context.Connected -> {
            context.reference
        }
        else -> {
            null
        }
    }

    val path: String? = when (context) {
        is Context.Connected -> {
            context.path
        }
        else -> {
            null
        }
    }

    val genericType: GenericType<*> = propertyDescriptor.genericType

    val propertyType: PropertyType = propertyDescriptor.propertyType

    sealed class Context {
        object Unconnected : Context()
        class Connected(val path: String, val reference: String?) : Context()
    }
}
