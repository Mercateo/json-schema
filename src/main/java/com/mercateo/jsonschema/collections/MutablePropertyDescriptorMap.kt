package com.mercateo.jsonschema.collections

import com.mercateo.jsonschema.generictype.GenericType
import com.mercateo.jsonschema.property.PropertyDescriptor
import com.mercateo.jsonschema.property.PropertyDescriptorDefault
import java.util.concurrent.ConcurrentHashMap

class MutablePropertyDescriptorMap {

    val map = ConcurrentHashMap<GenericType<*>, PropertyDescriptor<*>>()

    fun putAll(other: MutablePropertyDescriptorMap) {
        map.putAll(other.map)
    }

    fun <T> containsKey(genericType: GenericType<T>): Boolean {
        return map.containsKey(genericType)
    }

    operator fun <T> get(genericType: GenericType<T>): PropertyDescriptor<T> {
        return map[genericType] as PropertyDescriptor<T>
    }

    fun <T> put(genericType: GenericType<T>, propertyDescriptor: PropertyDescriptorDefault<T>) {
        map.put(genericType, propertyDescriptor)
    }

}
