package com.mercateo.jsonschema.schema

import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.generictype.GenericType
import com.mercateo.jsonschema.mapper.SchemaMapper
import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.property.PropertyBuilder
import com.mercateo.jsonschema.property.PropertyBuilderDefault
import com.mercateo.jsonschema.property.UnwrappedPropertyMapper

class SchemaGenerator {

    @JvmOverloads
    fun <T> generateSchema(elementClass : Class<T>, defaultValue: T? = null, allowedValues: Array<T>? = null,
                                          context: SchemaPropertyContext = SchemaPropertyContext()): ObjectNode {

        val propertyBuilder = createPropertyBuilder(context)

        val property = propertyBuilder.from(elementClass)

        val mapper = SchemaMapper()

        val objectContext = ObjectContext(property, defaultValue, if (allowedValues != null) setOf(*allowedValues) else emptySet())

        return mapper.toJson(objectContext)
    }

    private fun createPropertyBuilder(context: SchemaPropertyContext): PropertyBuilder {
        val unwrapAnnotations = context.unwrapAnnotations
        var propertyBuilder: PropertyBuilder = PropertyBuilderDefault(*context.propertyCollectors.toTypedArray())
        if (unwrapAnnotations.isNotEmpty()) {
            propertyBuilder = object : PropertyBuilder {

                val unwrappedPropertyMapper = UnwrappedPropertyMapper(*context.unwrapAnnotations.toTypedArray())

                override fun <T> from(propertyClass: Class<T>): Property<Void, T> {
                    return from(GenericType.of(propertyClass))
                }

                override fun <T> from(genericType: GenericType<T>): Property<Void, T> {
                    val property = propertyBuilder.from(genericType)
                    return unwrappedPropertyMapper.from(property)
                }
            }
        }
        return propertyBuilder
    }
}
