package com.mercateo.jsonschema.property

import java.util.concurrent.ConcurrentHashMap

class ReferencedPropertyMapper : PropertyMapper {

    private val knownRootElements: ConcurrentHashMap<String, Property> = ConcurrentHashMap()

    override fun from(property: Property): Property {
        return knownRootElements.computeIfAbsent(property.genericType.name, { addPathAndReference(property, "#", mutableMapOf()) })
    }

    private fun addPathAndReference(property: Property, path: String, knownPaths: MutableMap<String, String>): Property {
        val reference = knownPaths.get(property.genericType.name)

        if (reference == null && property.propertyType == PropertyType.OBJECT) {
            knownPaths.put(property.genericType.name, path)
        }

        val children = if (reference == null) {
            property.children.map { addPathAndReference(it, path + PATH_SEPARATOR + it.name, knownPaths) }
        } else {
            emptyList()
        }

        val propertyDescriptor = property.propertyDescriptor.copy(context = PropertyDescriptor.Context.Children(children))
        
        return property.copy(propertyDescriptor = propertyDescriptor, context = Property.Context.Connected(path, reference))
    }

    companion object {
        private val PATH_SEPARATOR = "/"
    }
}
