package com.mercateo.jsonschema.property.mapper

import com.mercateo.jsonschema.SchemaContext
import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.property.PropertyDescriptor
import com.mercateo.jsonschema.property.PropertyType
import java.util.concurrent.ConcurrentHashMap

class ReferencedPropertyMapper : PropertyMapper {

    private val knownRootElements: ConcurrentHashMap<String, Property<*, *>> = ConcurrentHashMap()

    override fun <S, T> from(property: Property<S, T>, schemaContext: SchemaContext): Property<S, T> {
        @Suppress("UNCHECKED_CAST")
        return knownRootElements.computeIfAbsent(property.genericType.name) {
            addPathAndReference(
                property,
                "#",
                mutableMapOf()
            )
        } as Property<S, T>
    }

    private fun <S, T> addPathAndReference(
        property: Property<S, T>,
        path: String,
        knownPaths: MutableMap<String, String>
    ): Property<S, T> {
        val typeName = property.genericType.name

        val reference = knownPaths[typeName]

        if (reference == null && property.propertyType == PropertyType.OBJECT) {
            knownPaths[typeName] = path
        }

        val connected = Property.Context.Connected(path, reference)
        return if (reference == null) {
            property.updateChildren(Property.Updater.Flat {
                addPathAndReference(
                    it,
                    path + PATH_SEPARATOR + it.name,
                    knownPaths
                )
            })
                .copy(context = connected)
        } else {
            property.copy(
                propertyDescriptor = property.propertyDescriptor.copy(variant = PropertyDescriptor.Variant.Reference),
                context = connected
            )
        }
    }

    companion object {
        private const val PATH_SEPARATOR = "/"
    }
}
