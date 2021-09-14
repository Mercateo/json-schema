package com.mercateo.jsonschema.mapper

import com.mercateo.jsonschema.SchemaGeneratorClasses
import com.mercateo.jsonschema.property.BasicPropertyBuilder
import com.mercateo.jsonschema.property.Property
import com.mercateo.jsonschema.property.from
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.function.Predicate

class PropertyCheckerTest {

    @Test
    fun checksPredicate() {
        val predicate = mockk<Predicate<Property<*, *>>>()
        val checker = PropertyChecker.fromPredicate(predicate)

        val propertyBuilder = BasicPropertyBuilder()

        val property = propertyBuilder.from(SchemaGeneratorClasses::Simple::class.java)

        every { predicate.test(property) } returns false

        assertThat(checker.test(property)).isFalse

        every { predicate.test(property) } returns true

        assertThat(checker.test(property)).isTrue
    }
}