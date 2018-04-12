package com.mercateo.jsonschema

import com.fasterxml.jackson.annotation.JsonUnwrapped
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

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"baar\":{\"type\":\"integer\"},\"bar\":{\"type\":\"integer\"},\"baz\":{\"type\":\"number\"},\"foo\":{\"type\":\"string\"},\"quux\":{\"type\":\"boolean\"},\"qux\":{\"type\":\"number\"}}}")
    }

    @Test
    fun shouldCreateSchemaForOtherTypes() {
        val schemaClass = SchemaGeneratorClasses.SimpleOther::class.java
        val schema = schemaGenerator.generateSchema(schemaClass)

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"bar\":{\"type\":\"number\"},\"baz\":{\"type\":\"number\"},\"foo\":{\"type\":\"integer\"}}}")
    }

    @Test
    fun shouldIgnoreStaticMethods() {
        val schemaClass = SchemaGeneratorClasses.Simple::class.java
        val schema = schemaGenerator.generateSchema(schemaClass)

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"baar\":{\"type\":\"integer\"},\"bar\":{\"type\":\"integer\"},\"baz\":{\"type\":\"number\"},\"foo\":{\"type\":\"string\"},\"quux\":{\"type\":\"boolean\"},\"qux\":{\"type\":\"number\"}}}")
    }

    @Test
    fun shouldCreateSchemaWithDefaultValues() {
        val schemaClass = SchemaGeneratorClasses.Simple::class.java

        val defaultValue = SchemaGeneratorClasses.Simple()
        defaultValue.foo = "foo"
        defaultValue.bar = 10
        defaultValue.baar = 100L
        defaultValue.baz = 4.8f
        defaultValue.qux = 5.1
        defaultValue.quux = true;

        val schema = schemaGenerator.generateSchema(schemaClass, defaultValue = defaultValue)

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"baar\":{\"type\":\"integer\",\"default\":100},\"bar\":{\"type\":\"integer\",\"default\":10},\"baz\":{\"type\":\"number\",\"default\":4.8},\"foo\":{\"type\":\"string\",\"default\":\"foo\"},\"quux\":{\"type\":\"boolean\",\"default\":true},\"qux\":{\"type\":\"number\",\"default\":5.1}}}")
    }

    @Test
    fun shouldCreateSchemaWithAllowedValues() {
        val schemaClass = SchemaGeneratorClasses.Simple::class.java

        val allowedValue = SchemaGeneratorClasses.Simple()
        allowedValue.foo = "foo"
        allowedValue.bar = 10;
        allowedValue.baar = 100L
        allowedValue.baz = 4.8f;
        allowedValue.qux = 5.1
        allowedValue.quux = true;

        val schema = schemaGenerator.generateSchema(schemaClass, allowedValues = arrayOf(allowedValue))

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"baar\":{\"type\":\"integer\",\"enum\":[100]},\"bar\":{\"type\":\"integer\",\"enum\":[10]},\"baz\":{\"type\":\"number\",\"enum\":[4.8]},\"foo\":{\"type\":\"string\",\"enum\":[\"foo\"]},\"quux\":{\"type\":\"boolean\",\"enum\":[true]},\"qux\":{\"type\":\"number\",\"enum\":[5.1]}}}")
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

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"bar\":{\"type\":\"string\"},\"baz\":{\"type\":\"string\"},\"foo\":{\"type\":\"string\"},\"qux\":{\"type\":\"string\",\"minLength\":1}},\"required\":[\"bar\",\"qux\"]}")
    }

    @Test
    fun showsIntValidationProperties() {
        val schema = schemaGenerator.generateSchema(SchemaGeneratorClasses.IntValidation::class.java)

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"max\":{\"type\":\"integer\",\"maximum\":10},\"min\":{\"type\":\"integer\",\"minimum\":5}}}")
    }

    @Test
    fun showsStringValidationProperties() {
        val schema = schemaGenerator.generateSchema(SchemaGeneratorClasses.StringValidation::class.java)

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"max\":{\"type\":\"string\",\"maxLength\":10},\"min\":{\"type\":\"string\",\"minLength\":5},\"range\":{\"type\":\"string\",\"maxLength\":9,\"minLength\":6}}}")
    }

    @Test
    fun showsIntegerValidationProperties() {
        val schema = schemaGenerator.generateSchema(SchemaGeneratorClasses.IntegerAnnotations::class.java)

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"max\":{\"type\":\"integer\",\"maximum\":10},\"min\":{\"type\":\"integer\",\"minimum\":5}}}")
    }

    @Test
    fun showsEnumValuesAllowedValues() {
        val schema = schemaGenerator.generateSchema(SchemaGeneratorClasses.EnumValue::class.java)

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"enumValue\":{\"type\":\"string\",\"enum\":[\"TRUE\",\"FALSE\"]}}}")
    }

    @Test
    fun showsAllowedValuesForEnum() {
        val allowedValues = arrayOf(SchemaGeneratorClasses.EnumValue().apply { enumValue = SchemaGeneratorClasses.Value.TRUE })
        val schema = schemaGenerator.generateSchema(SchemaGeneratorClasses.EnumValue::class.java, null, allowedValues)

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"enumValue\":{\"type\":\"string\",\"enum\":[\"TRUE\"]}}}")
    }

    @Test
    fun shouldAddUnwrappedPropertyMapper() {
        schemaGenerator = SchemaGenerator(propertyCollectors = listOf(FieldCollector()), unwrapAnnotations = listOf(JsonUnwrapped::class.java))

        val schema = schemaGenerator.generateSchema(SchemaGeneratorClasses.EnumValue::class.java, null, null)

        assertThat(schema.toString()).isEqualTo("{\"type\":\"object\",\"properties\":{\"enumValue\":{\"type\":\"string\",\"enum\":[\"TRUE\",\"FALSE\"]}}}")
    }
}