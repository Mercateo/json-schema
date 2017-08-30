package com.mercateo.jsonschema.property

import java.util.concurrent.ConcurrentHashMap

class ReferencedPropertyMapper : PropertyMapper {

    private val knownRootElements: ConcurrentHashMap<String, Property<*, *>> = ConcurrentHashMap()

    override fun <S, T> from(property: Property<S, T>): Property<S, T> {
        @Suppress("UNCHECKED_CAST")
        return knownRootElements.computeIfAbsent(property.genericType.name, { addPathAndReference(property, "#", mutableMapOf()) }) as Property<S, T>
    }

    private fun <S, T> addPathAndReference(property: Property<S, T>, path: String, knownPaths: MutableMap<String, String>): Property<S, T> {
        val typeName = property.genericType.name

        val reference = knownPaths.get(typeName)

        if (reference == null && property.propertyType == PropertyType.OBJECT) {
            knownPaths.put(typeName, path)
        }

        val children = if (reference == null) {
            property.children.map { addPathAndReference(it, path + PATH_SEPARATOR + it.name, knownPaths) }
        } else {
            emptyList()
        }

        val propertyDescriptor = (property.propertyDescriptor as PropertyDescriptorDefault).copy(context = PropertyDescriptor.Context.Children(children))

        return property.copy(propertyDescriptor = propertyDescriptor, context = Property.Context.Connected(path, reference))
    }

    companion object {
        private val PATH_SEPARATOR = "/"
    }
}
