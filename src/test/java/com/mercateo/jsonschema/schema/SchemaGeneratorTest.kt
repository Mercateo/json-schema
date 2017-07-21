package com.mercateo.jsonschema.schema

import org.junit.Before
import org.junit.Test

class SchemaGeneratorTest {

    lateinit var schemaGenerator: SchemaGenerator

    @Before
    fun setUp() {
        schemaGenerator = SchemaGenerator()
    }

    @Test
    fun name() {
        schemaGenerator.generateSchema(String::class.java)
    }
}