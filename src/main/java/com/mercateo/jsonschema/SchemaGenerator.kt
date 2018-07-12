package com.mercateo.jsonschema

import com.fasterxml.jackson.databind.node.ObjectNode
import com.mercateo.jsonschema.mapper.ObjectContext
import com.mercateo.jsonschema.mapper.PropertyChecker
import com.mercateo.jsonschema.mapper.SchemaMapper
import com.mercateo.jsonschema.property.*
import com.mercateo.jsonschema.property.collector.MethodCollector
import java.util.function.Function

class SchemaGenerator(
        unwrapAnnotations: List<UnwrappedPropertyUpdater<*>> = emptyList(),
        propertyCollectors: List<RawPropertyCollector> = listOf(MethodCollector()),
        customUnwrappers: Map<Class<*>, Function<Any, Any?>> = emptyMap()
) {

    private val propertyBuilder: PropertyBuilder

    private val schemaMapper: SchemaMapper

    init {
        val convertedCustomUnwrappers: Map<Class<*>, (Any) -> Any?> = customUnwrappers.mapValues { entry -> { x: Any -> entry.value.apply(x) } }

        propertyBuilder = createPropertyBuilder(propertyCollectors, unwrapAnnotations, convertedCustomUnwrappers)
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
                                      unwrapAnnotations: List<UnwrappedPropertyUpdater<*>>,
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
