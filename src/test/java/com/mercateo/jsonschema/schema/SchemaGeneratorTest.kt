package com.mercateo.jsonschema.schema

import com.mercateo.jsonschema.property.FieldCollector
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.util.function.Predicate

class SchemaGeneratorTest {

    private lateinit var schemaGenerator: SchemaGenerator

    @Before
    fun setUp() {
        schemaGenerator = SchemaGenerator()
    }

    @Test
    fun shouldCreateSchema() {
        val schemaPropertyContext = SchemaPropertyContext(PropertyChecker.fromPredicate(Predicate { property -> true }), emptyList(), listOf(FieldCollector()))

        val schemaClass = SchemaGeneratorClasses.Simple::class.java
        val schema = schemaGenerator.generateSchema(schemaClass, context = schemaPropertyContext)

        assertThat(schema).isEqualTo("{\"type\":\"object\",\"properties\":{\"foo\":{\"type\":\"string\"},\"bar\":{\"type\":\"integer\"},\"baz\":{\"type\":\"number\"},\"qux\":{\"type\":\"boolean\",\"default\":false}}}")
    }

    @Test
    fun shouldIgnoreStaticMethods() {
        val schemaPropertyContext = SchemaPropertyContext(PropertyChecker.fromPredicate(Predicate { property -> true }), emptyList(), listOf(FieldCollector()))

        val schemaClass = SchemaGeneratorClasses.Simple::class.java
        val schema = schemaGenerator.generateSchema(schemaClass, context = schemaPropertyContext)

        assertThat(schema).isEqualTo("{\"type\":\"object\",\"properties\":{\"foo\":{\"type\":\"string\"},\"bar\":{\"type\":\"integer\"},\"baz\":{\"type\":\"number\"},\"qux\":{\"type\":\"boolean\",\"default\":false}}}")
    }

    @Test
    fun shouldCreateSchemaWithDefaultValues() {
        val schemaPropertyContext = SchemaPropertyContext(PropertyChecker.fromPredicate(Predicate { property -> true }), emptyList(), listOf(FieldCollector()))

        val schemaClass = SchemaGeneratorClasses.Simple::class.java

        val defaultValue = SchemaGeneratorClasses.Simple()
        defaultValue.foo = "foo"
        defaultValue.bar = 10;
        defaultValue.baz = 4.8f;
        defaultValue.qux = true;

        val schema = schemaGenerator.generateSchema(schemaClass, defaultValue = defaultValue, context = schemaPropertyContext)

        assertThat(schema).isEqualTo("{\"type\":\"object\",\"properties\":{\"foo\":{\"type\":\"string\",\"default\":\"foo\"},\"bar\":{\"type\":\"integer\",\"default\":10},\"baz\":{\"type\":\"number\",\"default\":4.8},\"qux\":{\"type\":\"boolean\",\"default\":true}}}")
    }

    @Test
    fun shouldCreateSchemaWithAllowedValues() {
        val schemaPropertyContext = SchemaPropertyContext(PropertyChecker.fromPredicate(Predicate { property -> true }), emptyList(), listOf(FieldCollector()))

        val schemaClass = SchemaGeneratorClasses.Simple::class.java

        val allowedValue = SchemaGeneratorClasses.Simple()
        allowedValue.foo = "foo"
        allowedValue.bar = 10;
        allowedValue.baz = 4.8f;
        allowedValue.qux = true;

        val schema = schemaGenerator.generateSchema(schemaClass, allowedValues = arrayOf(allowedValue), context = schemaPropertyContext)

        assertThat(schema).isEqualTo("{\"type\":\"object\",\"properties\":{\"foo\":{\"type\":\"string\",\"enum\":[\"foo\"]},\"bar\":{\"type\":\"integer\",\"enum\":[10]},\"baz\":{\"type\":\"number\",\"enum\":[4.8]},\"qux\":{\"type\":\"boolean\",\"default\":false,\"enum\":[true]}}}")
    }

}