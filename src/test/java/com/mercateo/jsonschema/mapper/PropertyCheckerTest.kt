package com.mercateo.jsonschema.mapper

import com.mercateo.jsonschema.SchemaGeneratorClasses
import com.mercateo.jsonschema.property.BasicPropertyBuilder
import com.mercateo.jsonschema.property.Property
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.function.Predicate

class PropertyCheckerTest {

    @Test
    fun checksPredicate() {
        val predicate = mock<Predicate<Property<*, *>>>()
        val checker = PropertyChecker.fromPredicate(predicate)

        val propertyBuilder = BasicPropertyBuilder()

        val property = propertyBuilder.from(SchemaGeneratorClasses::Simple::class.java)
        assertThat(checker.test(property)).isFalse()

        whenever(predicate.test(property)).thenReturn(true)

        assertThat(checker.test(property)).isTrue()
    }
}