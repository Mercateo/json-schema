package com.mercateo.jsonschema.property

import com.mercateo.jsonschema.generictype.GenericType

data class PropertyDescriptorDefault(
        override val propertyType : PropertyType,
        override val genericType : GenericType<*>,
        override val children : List<Property>,
        override val annotations : Map<Class<out Annotation>, Set<Annotation>>
) : PropertyDescriptor {
}
