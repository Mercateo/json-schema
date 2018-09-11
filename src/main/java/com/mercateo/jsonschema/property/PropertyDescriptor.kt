package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType


data class PropertyDescriptor<T>(
        val propertyType: PropertyType,
        val genericType: GenericType<T>,
        val variant: PropertyDescriptor.Variant,
        val annotations: Map<Class<out Annotation>, Set<Annotation>>
) {
    @Suppress("UNCHECKED_CAST")
    val children: List<Property<T, Any>> =
            when (variant) {
                is PropertyDescriptor.Variant.Properties<*> -> variant.children as List<Property<T, Any>>
                is PropertyDescriptor.Variant.Polymorphic -> variant.elements as List<Property<T, Any>>
                else -> emptyList()
            }

    sealed class Variant {
        open fun filter(predicate: (Property<Any, Any>) -> Boolean): Variant = this
        open fun update(updateVisitor: UpdateVisitor): Variant = this

        class Properties<T>(val children: List<Property<T, Any>>) : Variant() {
            override fun filter(predicate: (Property<Any, Any>) -> Boolean): Variant =
                    Properties((children as List<Property<Any, Any>>).filter(predicate).map { it.filterChildren(predicate) })


            override fun update(updateVisitor: UpdateVisitor): Variant =
                    Properties((children as List<Property<Any, Any>>).map { updateVisitor.update(it) })

            override fun toString(): String {
                return "Properties(elements=${children.joinToString(", ", transform = { it.name })})"
            }
        }

        class Polymorphic(val elements: List<Property<Any, Any>>) : Variant() {
            override fun filter(predicate: (Property<Any, Any>) -> Boolean): Variant =
                    Polymorphic(elements.filter(predicate).map { it.filterChildren(predicate) })

            override fun update(updateVisitor: UpdateVisitor): Variant =
                    Polymorphic(elements.map { updateVisitor.update(it) })

            override fun toString(): String {
                return "Polymorphic(elements=${elements.joinToString(", ", transform = { it.name })})"
            }
        }

        object Primitive : Variant() {
            override fun toString(): String {
                return "primitive"
            }
        }

        object Reference : Variant() {
            override fun toString(): String {
                return "reference"
            }
        }
    }

    fun filter(predicate: (Property<Any, Any>) -> Boolean): PropertyDescriptor<T> =
            copy(variant = variant.filter(predicate))

    fun update(updateVisitor: UpdateVisitor): PropertyDescriptor<T> =
            copy(variant = variant.update(updateVisitor))

    sealed class UpdateVisitor(protected val propertyMapper: (Property<Any, Any>) -> Property<Any, Any>) {
        open fun update(property: Property<Any, Any>): Property<Any, Any> {
            return propertyMapper.invoke(property)
        }

        class Recursive(propertyMapper: (Property<Any, Any>) -> Property<Any, Any>) : UpdateVisitor(propertyMapper) {
            override fun update(property: Property<Any, Any>): Property<Any, Any> {
                return super.update(property.updateChildren(updateVisitor = this))
            }
        }

        class Flat(propertyMapper: (Property<Any, Any>) -> Property<Any, Any>) : UpdateVisitor(propertyMapper)
    }
}
