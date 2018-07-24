package com.mercateo.jsonschema.property.mapper

import com.mercateo.jsonschema.SchemaContext
import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.property.PropertyDescriptor


class CheckedPropertyMapper : PropertyMapper {
    override fun <S, T> from(property: Property<S, T>, schemaContext: SchemaContext): Property<S, T> {
        return when (property.propertyDescriptor.context) {
            is PropertyDescriptor.Context.Children<*> -> {

                return property.copy(
                        propertyDescriptor = property.propertyDescriptor.copy(
                                context = PropertyDescriptor.Context.Children(
                                        property.children
                                                .filter { schemaContext.propertyChecker.test(it) }
                                                .map { from(it, schemaContext) }
                                )
                        )
                )
            }
            else -> property
        }
    }
}