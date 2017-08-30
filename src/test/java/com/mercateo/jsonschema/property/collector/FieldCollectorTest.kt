package com.mercateo.jsonschema.property.collector

import com.mercateo.jsonschema.property.RawProperty
import com.mercateo.jsonschema.property.collector.FieldCollectorClasses.PropertyHolder
import com.mercateo.jsonschema.property.collector.FieldCollectorClasses.StaticPropertyHolder
import com.mercateo.jsonschema.property.collector.FieldCollector
import com.mercateo.jsonschema.property.collector.FieldCollectorConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class FieldCollectorTest {

    @Retention(AnnotationRetention.RUNTIME)
    annotation class Annotation1

    private lateinit var fieldCollector: FieldCollector

    @Before
    @Throws(Exception::class)
    fun setUp() {
        fieldCollector = FieldCollector(FieldCollectorConfig(includePrivateFields = true))
    }

    @Test
    @Throws(Exception::class)
    fun mapAllDeclaredFields() {
        val properties = fieldCollector.forType(PropertyHolder::class.java).toList()

        assertThat(properties).extracting("name").containsExactlyInAnyOrder("hidden",
                "visible")
    }

    @Test
    @Throws(Exception::class)
    fun limitToPublicFieldsIfConfigured() {
        fieldCollector = FieldCollector(FieldCollectorConfig(includePrivateFields = false))
        val properties = fieldCollector.forType(PropertyHolder::class.java).toList()

        assertThat(properties).extracting("name").containsExactly("visible")
    }

    @Test
    @Throws(Exception::class)
    fun mapAllDaeclaredFields() {
        @Suppress("UNCHECKED_CAST")
        val hidden: RawProperty<PropertyHolder, String> = fieldCollector.forType(PropertyHolder::class.java).filter { p -> p.name == "hidden" }.first()
                as RawProperty<PropertyHolder, String>

        val propertyHolder = PropertyHolder()
        hidden.valueAccessor(propertyHolder)
    }

    @Test
    fun ignoresStaticFields() {
        val properties = fieldCollector.forType(StaticPropertyHolder::class.java).toList()

        assertThat(properties).extracting("name").containsExactly("property")
    }
}
