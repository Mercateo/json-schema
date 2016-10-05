package com.mercateo.jsonschema.mapper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.jsonschema.property.Property;
import com.mercateo.jsonschema.property.PropertyDescriptor;
import com.mercateo.jsonschema.property.PropertyDescriptorReference;

public class SchemaMapper {

    ObjectNodeFactory objectNodeFactory = new ObjectNodeFactory();

    public ObjectNode map(Property property) {
        final ObjectNode result = objectNodeFactory.createNode();

        final PropertyDescriptor propertyDescriptor = property.propertyDescriptor();

        if (propertyDescriptor instanceof PropertyDescriptorReference) {
            PropertyDescriptorReference descriptorReference = (PropertyDescriptorReference) propertyDescriptor;

            result.put("$ref", descriptorReference.reference());
            return result;
        } else {
            switch (propertyDescriptor.propertyType()) {
                case OBJECT:
                    break;
            }
        }

        return result;
    }
}
