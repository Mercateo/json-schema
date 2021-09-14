package com.mercateo.jsonschema

import com.mercateo.jsonschema.property.Property
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SchemaContextTest {

    @Test
    fun shouldAllowAllPropertiesByDefault() {
        val uut = SchemaContext()

        val result = uut.propertyChecker.test(mockk<Property<Unit, Unit>>())

        assertThat(result).isTrue
    }
}