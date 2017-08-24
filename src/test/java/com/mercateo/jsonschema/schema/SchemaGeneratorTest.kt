package com.mercateo.jsonschema.schema

import com.mercateo.jsonschema.property.MethodCollector
import org.junit.Before
import org.junit.Test
import java.util.function.Predicate

import org.assertj.core.api.Assertions.*

class SchemaGeneratorTest {

    private lateinit var schemaGenerator: SchemaGenerator

    @Before
    fun setUp() {
        schemaGenerator = SchemaGenerator()
    }

    @Test
    fun shouldCreateSchema() {
        val schemaPropertyContext = SchemaPropertyContext(PropertyChecker.fromPredicate(Predicate { property -> true }), emptyList(), listOf(MethodCollector()))

        val schemaClass = SchemaGeneratorClasses.Simple::class.java
        val schema = schemaGenerator.generateSchema(schemaClass, defaultValue = null, allowedValues = emptyList(), context = schemaPropertyContext )

        assertThat(schema).isEqualTo("{\"type\":\"object\",\"properties\":{\"foo\":{\"type\":\"string\"},\"bar\":{\"type\":\"integer\"}}}")
    }

    @Test
    fun shouldCreateSchemaWithDefaultValues() {
        val schemaPropertyContext = SchemaPropertyContext(PropertyChecker.fromPredicate(Predicate { property -> true }), emptyList(), listOf(MethodCollector()))

        val schemaClass = SchemaGeneratorClasses.Simple::class.java
        val defaultValue = SchemaGeneratorClasses.Simple()
        defaultValue.bar = 10;
        defaultValue.foo = "foo"

        val schema = schemaGenerator.generateSchema(schemaClass, defaultValue = defaultValue, allowedValues = emptyList(), context = schemaPropertyContext )

        assertThat(schema).isEqualTo("{\"type\":\"object\",\"properties\":{\"foo\":{\"type\":\"string\",\"default\":\"foo\"},\"bar\":{\"type\":\"integer\",\"default\":12}}}")
    }

}