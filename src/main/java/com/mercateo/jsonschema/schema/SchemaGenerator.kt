package com.mercateo.jsonschema.schema

import com.mercateo.jsonschema.generictype.GenericType
import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.property.PropertyBuilder
import com.mercateo.jsonschema.property.PropertyBuilderDefault
import com.mercateo.jsonschema.property.UnwrappedPropertyMapper

class SchemaGenerator {

    fun generateSchema(objectContext: ObjectContext<*>,
                       context: SchemaPropertyContext): JsonPropertyResult? {

        val propertyBuilder = createPropertyBuilder(context)

        val property = propertyBuilder.from(objectContext.javaClass)
        return null
    }

    private fun createPropertyBuilder(context: SchemaPropertyContext): PropertyBuilder {
        val unwrapAnnotations = context.unwrapAnnotations
        var propertyBuilder: PropertyBuilder = PropertyBuilderDefault(*context.propertyCollectors.toTypedArray())
        if (unwrapAnnotations.size > 0) {
            propertyBuilder = object : PropertyBuilder {

                val unwrappedPropertyMapper = UnwrappedPropertyMapper(*context.unwrapAnnotations.toTypedArray())

                override fun from(propertyClass: Class<*>): Property {
                    return from(GenericType.of(propertyClass))
                }

                override fun from(genericType: GenericType<*>): Property {
                    val property = propertyBuilder.from(genericType)
                    return unwrappedPropertyMapper.from(property)
                }
            }

        }
        return propertyBuilder
    }
}
