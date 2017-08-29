package com.mercateo.jsonschema.mapper

import com.mercateo.jsonschema.property.Property
import java.util.*

class ReferencedElementCollector {

    fun collectReferencedElements(rootProperty: Property<*, *>): Set<String> {

        val referencedElements: MutableSet<String> = mutableSetOf()

        val queue = Stack<Property<*, *>>()

        queue.push(rootProperty)

        while (queue.isNotEmpty()) {
            val property = queue.pop()

            if (property.reference != null) {
                referencedElements.add(property.reference)
            }

            property.children.forEach { queue.add(it) }
        }

        return referencedElements.toSet()
    }
}