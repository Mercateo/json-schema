package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType

data class Property<in S, T>(
        val name: String,
        val propertyDescriptor: PropertyDescriptor<T>,
        val valueAccessor: (S) -> T?,
        val annotations: Map<Class<out Annotation>, Set<Annotation>>,
        val context: Context = Property.Context.Unconnected
) {
    fun getValue(instance: S): T? {
        return valueAccessor(instance)
    }

    fun filterChildren(predicate: (Property<Any, Any>) -> Boolean): Property<S, T> {
        return copy(propertyDescriptor= propertyDescriptor.filter(predicate))
    }

    fun update(updater: Updater) : Property<S, T> {
        return updater.update(this as Property<Any, Any>) as Property<S, T>
    }

    fun updateChildren(updater: Updater) : Property<S, T> {
        return copy(propertyDescriptor = propertyDescriptor.update(updater))
    }

    val children: List<Property<T, Any>> = propertyDescriptor.children

    val reference: String? = context.reference

    val path: String? = context.path

    val genericType: GenericType<T> = propertyDescriptor.genericType

    val propertyType: PropertyType = propertyDescriptor.propertyType

    sealed class Context(val path: String? = null, val reference: String? = null) {
        object Unconnected : Context()

        class Connected(path: String, reference: String?) : Context(path, reference) {
            override fun toString(): String {
                return "Connected(path='$path', reference='$reference')"
            }
        }
    }

    sealed class Updater(protected val propertyMapper: (Property<Any, Any>) -> Property<Any, Any>) {
        open fun update(property: Property<Any, Any>): Property<Any, Any> {
            return propertyMapper.invoke(property)
        }

        class Recursive(propertyMapper: (Property<Any, Any>) -> Property<Any, Any>) : Updater(propertyMapper) {
            override fun update(property: Property<Any, Any>): Property<Any, Any> {
                return super.update(property.updateChildren(updater = this))
            }
        }

        class Flat(propertyMapper: (Property<Any, Any>) -> Property<Any, Any>) : Updater(propertyMapper)
    }
}
