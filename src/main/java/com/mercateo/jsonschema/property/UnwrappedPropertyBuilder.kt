package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType
import java.util.concurrent.ConcurrentHashMap

class UnwrappedPropertyBuilder
@SafeVarargs
constructor(private val propertyBuilder: PropertyBuilder, vararg unwrapAnnotations: Class<out Annotation>) : PropertyBuilder {

    private val unwrapAnnotations: Set<Class<out Annotation>>

    private val unwrappedProperties: ConcurrentHashMap<GenericType<*>, Property>

    private val unwrappedPropertyDescriptors: ConcurrentHashMap<GenericType<*>, PropertyDescriptor>

    init {
        this.unwrapAnnotations = setOf(*unwrapAnnotations)

        this.unwrappedProperties = ConcurrentHashMap<GenericType<*>, Property>()
        this.unwrappedPropertyDescriptors = ConcurrentHashMap<GenericType<*>, PropertyDescriptor>()
    }

    override fun from(clazz: Class<*>): Property {
        return from(GenericType.of(clazz))
    }

    override fun from(genericType: GenericType<*>): Property {
        return unwrappedProperties.computeIfAbsent(genericType, { unwrap(it) })
    }

    private fun unwrap(genericType: GenericType<*>): Property {
        val addedUnwrappedPropertyDescriptors = mutableMapOf<GenericType<*>, PropertyDescriptor>()
        val property = unwrap(propertyBuilder.from(genericType), addedUnwrappedPropertyDescriptors)
        unwrappedPropertyDescriptors.putAll(addedUnwrappedPropertyDescriptors)
        return property
    }

    private fun unwrap(
            property: Property,
            addedUnwrappedProperties: MutableMap<GenericType<*>, PropertyDescriptor>
    ): Property {
        val genericType = property.genericType

        val propertyDescriptor =
                if (unwrappedPropertyDescriptors.containsKey(genericType)) {
                    unwrappedPropertyDescriptors[genericType]!!
                } else {
                    if (addedUnwrappedProperties.containsKey(genericType)) {
                        addedUnwrappedProperties[genericType]!!
                    } else {
                        createDescriptor(property, addedUnwrappedProperties)
                    }
                }
        return Property(property.name, propertyDescriptor, property.valueAccessor, property.annotations)
    }

    private fun createDescriptor(
            property: Property,
            addedUnwrappedProperties: MutableMap<GenericType<*>, PropertyDescriptor>
    ): PropertyDescriptor {
        val children = getChildren(property, addedUnwrappedProperties)
        val propertyType = property.propertyType
        val genericType = property.genericType
        val propertyDescriptor = PropertyDescriptorDefault(propertyType, genericType, children, property.propertyDescriptor.annotations)
        addedUnwrappedProperties.put(genericType, propertyDescriptor)
        return propertyDescriptor
    }

    private fun getChildren(
            property: Property,
            addedUnwrappedPropertyDescriptors: MutableMap<GenericType<*>, PropertyDescriptor>
    ): List<Property> {
        return property.children
                .map {
                    unwrap(it, addedUnwrappedPropertyDescriptors)
                }
                .flatMap {
                    val doUnwrapChild = !unwrapAnnotations.intersect(it.annotations.keys).isEmpty()
                    if (doUnwrapChild) {
                        getChildren(updateValueAccessors(it), addedUnwrappedPropertyDescriptors)
                    } else {
                        listOf(it)
                    }
                }
    }

    private fun updateValueAccessors(child: Property): Property {
        val children = child.children.map {
            Property(it.name, it.propertyDescriptor,
                    { instance ->
                        val intermediateObject = child.valueAccessor(instance)
                        if (intermediateObject != null) it.valueAccessor(intermediateObject) else null
                    }, it.annotations)
        }

        val propertyDescriptor = child.propertyDescriptor
        val updatedPropertyDescriptor = PropertyDescriptorDefault(
                propertyDescriptor.propertyType,
                propertyDescriptor.genericType,
                children,
                propertyDescriptor.annotations)
        return Property(child.name, updatedPropertyDescriptor, child.valueAccessor, child.annotations)
    }

}
