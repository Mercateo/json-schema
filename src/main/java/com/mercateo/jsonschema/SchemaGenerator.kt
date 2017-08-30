package com.mercateo.jsonschema

import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.SchemaMapper
import com.mercateo.jsonschema.property.*
import com.mercateo.jsonschema.property.collector.MethodCollector
import com.mercateo.jsonschema.mapper.ObjectContext
import com.mercateo.jsonschema.mapper.PropertyChecker

class SchemaGenerator(
        unwrapAnnotations: List<Class<out Annotation>> = emptyList(),
        propertyCollectors: List<RawPropertyCollector> = listOf(MethodCollector()),
        customUnwrappers: Map<Class<*>, (Any) -> Any?> = emptyMap()
) {

    private val propertyBuilder: PropertyBuilder

    private val schemaMapper: SchemaMapper

    init {
        propertyBuilder = createPropertyBuilder(propertyCollectors, unwrapAnnotations, customUnwrappers)
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

    private fun createPropertyBuilder(propertyCollectors: List<RawPropertyCollector>,
                                      unwrapAnnotations: List<Class<out Annotation>>,
                                      customUnwrappers: Map<Class<*>, (Any) -> Any?>): PropertyBuilder {
        var propertyBuilder: PropertyBuilder = BasicPropertyBuilder(customUnwrappers, *propertyCollectors.toTypedArray())

        var propertyMappers: MutableList<PropertyMapper> = mutableListOf()

        PropertyBuilderWrapper(propertyBuilder)
        if (unwrapAnnotations.isNotEmpty()) {
            propertyMappers.add(UnwrappedPropertyMapper(*unwrapAnnotations.toTypedArray()))
        }
        propertyMappers.add(ReferencedPropertyMapper())

        return PropertyBuilderWrapper(propertyBuilder, *propertyMappers.toTypedArray())
    }

    companion object {
        val defaultPropertyChecker: PropertyChecker = object : PropertyChecker {
            override fun test(t: Property<*, *>): Boolean {
                return true
            }
        }
    }

}
