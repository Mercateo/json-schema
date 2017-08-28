package com.mercateo.jsonschema.schema

import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.generictype.GenericType
import com.mercateo.jsonschema.mapper.SchemaMapper
import com.mercateo.jsonschema.property.*

class SchemaGenerator(
        unwrapAnnotations: List<Class<out Annotation>> = emptyList(),
        propertyCollectors: List<RawPropertyCollector> = listOf(MethodCollector())
) {

    private val propertyBuilder: PropertyBuilder

    private val schemaMapper: SchemaMapper

    init {
        propertyBuilder = createPropertyBuilder(propertyCollectors, unwrapAnnotations)
        schemaMapper = SchemaMapper()
    }

    @JvmOverloads
    fun <T> generateSchema(elementClass: Class<T>, defaultValue: T? = null, allowedValues: Array<T>? = null,
                           propertyChecker: PropertyChecker = defaultPropertyChecker
    ): ObjectNode {

        val property = propertyBuilder.from(elementClass)

        val objectContext = ObjectContext(property, defaultValue, if (allowedValues != null) setOf(*allowedValues) else emptySet())

        return schemaMapper.toJson(objectContext)
    }

    private fun createPropertyBuilder(propertyCollectors: List<RawPropertyCollector>, unwrapAnnotations: List<Class<out Annotation>>): PropertyBuilder {
        var propertyBuilder: PropertyBuilder = PropertyBuilderDefault(*propertyCollectors.toTypedArray())
        if (unwrapAnnotations.isNotEmpty()) {
            propertyBuilder = object : PropertyBuilder {

                val unwrappedPropertyMapper = UnwrappedPropertyMapper(*unwrapAnnotations.toTypedArray())

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

    companion object {
        val defaultPropertyChecker: PropertyChecker = object : PropertyChecker {
            override fun test(t: Property<*, *>): Boolean {
                return true
            }
        }
    }

}
