package com.mercateo.jsonschema

import com.mercateo.jsonschema.property.collector.FieldCollector
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class SchemaGeneratorTest {

    private lateinit var schemaGenerator: SchemaGenerator

    @Before
    fun setUp() {
        schemaGenerator = SchemaGenerator(propertyCollectors = listOf(FieldCollector()))
    }

    @Test
    fun shouldCreateSchema() {
        val schemaClass = SchemaGeneratorClasses.Simple::class.java
        val schema = schemaGenerator.generateSchema(schemaClass)

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"bar\":{\"type\":\"integer\"},\"baz\":{\"type\":\"number\"},\"foo\":{\"type\":\"string\"},\"qux\":{\"type\":\"boolean\",\"default\":false}}}")
    }

    @Test
    fun shouldIgnoreStaticMethods() {
        val schemaClass = SchemaGeneratorClasses.Simple::class.java
        val schema = schemaGenerator.generateSchema(schemaClass)

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"bar\":{\"type\":\"integer\"},\"baz\":{\"type\":\"number\"},\"foo\":{\"type\":\"string\"},\"qux\":{\"type\":\"boolean\",\"default\":false}}}")
    }

    @Test
    fun shouldCreateSchemaWithDefaultValues() {
        val schemaClass = SchemaGeneratorClasses.Simple::class.java

        val defaultValue = SchemaGeneratorClasses.Simple()
        defaultValue.foo = "foo"
        defaultValue.bar = 10;
        defaultValue.baz = 4.8f;
        defaultValue.qux = true;

        val schema = schemaGenerator.generateSchema(schemaClass, defaultValue = defaultValue)

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"bar\":{\"type\":\"integer\",\"default\":10},\"baz\":{\"type\":\"number\",\"default\":4.8},\"foo\":{\"type\":\"string\",\"default\":\"foo\"},\"qux\":{\"type\":\"boolean\",\"default\":true}}}")
    }

    @Test
    fun shouldCreateSchemaWithAllowedValues() {
        val schemaClass = SchemaGeneratorClasses.Simple::class.java

        val allowedValue = SchemaGeneratorClasses.Simple()
        allowedValue.foo = "foo"
        allowedValue.bar = 10;
        allowedValue.baz = 4.8f;
        allowedValue.qux = true;

        val schema = schemaGenerator.generateSchema(schemaClass, allowedValues = arrayOf(allowedValue))

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"bar\":{\"type\":\"integer\",\"enum\":[10]},\"baz\":{\"type\":\"number\",\"enum\":[4.8]},\"foo\":{\"type\":\"string\",\"enum\":[\"foo\"]},\"qux\":{\"type\":\"boolean\",\"default\":false,\"enum\":[true]}}}")
    }

    @Test
    fun handlesJavaOptional() {
        val schema = schemaGenerator.generateSchema(SchemaGeneratorClasses.OptionTypes::class.java)

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"optionalString\":{\"type\":\"string\"}}}")
    }

    @Test
    fun handlesJavaCollections() {
        val schema = schemaGenerator.generateSchema(SchemaGeneratorClasses.Collections::class.java)

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"strings\":{\"type\":\"array\",\"items\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}}}}}")
    }

    @Test
    fun handlesReferences() {
        val schema = schemaGenerator.generateSchema(SchemaGeneratorClasses.References::class.java)

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"bar\":{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"string\"},\"name\":{\"type\":\"string\"}},\"id\":\"#/bar\"},\"foo\":{\"\$ref\":\"#/bar\"}}}")
    }

    @Test
    fun handlesClassHierarchies() {
        val schema = schemaGenerator.generateSchema(SchemaGeneratorClasses.Subclass::class.java)

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"bar\":{\"type\":\"string\"},\"foo\":{\"type\":\"string\"},\"qux\":{\"type\":\"string\"}}}")
    }

    @Test
    fun showsRequiredProperties() {
        val schema = schemaGenerator.generateSchema(SchemaGeneratorClasses.Required::class.java)

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"bar\":{\"type\":\"string\"},\"baz\":{\"type\":\"string\"},\"foo\":{\"type\":\"string\"},\"qux\":{\"type\":\"string\"}},\"required\":[\"bar\",\"qux\"]}")
    }
}