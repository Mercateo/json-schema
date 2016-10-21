package com.mercateo.jsonschema.schema

import com.mercateo.jsonschema.property.PropertyBuilder
import com.mercateo.jsonschema.property.PropertyBuilderDefault
import com.mercateo.jsonschema.property.UnwrappedPropertyBuilder

class SchemaGenerator {

    fun generateSchema(objectContext: ObjectContext<*>,
                       context: SchemaPropertyContext): JsonPropertyResult? {

        val propertyBuilder = createPropertyBuilder(context)

        val property = propertyBuilder.from(objectContext.javaClass)
        return null
    }

    private fun createPropertyBuilder(context: SchemaPropertyContext): PropertyBuilder {
        val unwrapAnnotations = context.unwrapAnnotations
        var propertyBuilder: PropertyBuilder = PropertyBuilderDefault(context.propertyCollectors)
        if (unwrapAnnotations.size > 0) {
            propertyBuilder = UnwrappedPropertyBuilder(propertyBuilder)
        }
        return propertyBuilder
    }
}
