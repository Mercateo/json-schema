package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType


data class PropertyDescriptor<T>(
    val propertyType: PropertyType,
    val genericType: GenericType<T>,
    val variant: Variant,
    val annotations: Map<Class<out Annotation>, Set<Annotation>>
) {
    @Suppress("UNCHECKED_CAST")
    val children: List<Property<T, Any>> =
        when (variant) {
            is Variant.Properties<*> -> variant.children as List<Property<T, Any>>
            is Variant.Polymorphic -> variant.elements as List<Property<T, Any>>
            else -> emptyList()
        }

    sealed class Variant {
        open fun filter(predicate: (Property<Any, Any>) -> Boolean): Variant = this
        open fun update(updater: Property.Updater): Variant = this

        class Properties<T>(val children: List<Property<T, Any>>) : Variant() {
            override fun filter(predicate: (Property<Any, Any>) -> Boolean): Variant =
                Properties(
                    (children as List<Property<Any, Any>>).filter(predicate).map { it.filterChildren(predicate) })


            override fun update(updater: Property.Updater): Variant =
                Properties((children as List<Property<Any, Any>>).map { updater.update(it) })

            override fun toString(): String {
                return "Properties(elements=${children.joinToString(", ", transform = { it.name })})"
            }
        }

        class Polymorphic(val elements: List<Property<Any, Any>>) : Variant() {
            override fun filter(predicate: (Property<Any, Any>) -> Boolean): Variant =
                Polymorphic(elements.filter(predicate).map { it.filterChildren(predicate) })

            override fun update(updater: Property.Updater): Variant =
                Polymorphic(elements.map { updater.update(it) })

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

    fun update(updater: Property.Updater): PropertyDescriptor<T> =
        copy(variant = variant.update(updater))

}
