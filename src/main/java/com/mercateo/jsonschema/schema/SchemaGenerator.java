package com.mercateo.jsonschema.schema;

import com.mercateo.jsonschema.property.Property;
import com.mercateo.jsonschema.property.PropertyBuilder;
import com.mercateo.jsonschema.property.PropertyBuilderDefault;
import com.mercateo.jsonschema.property.UnwrappedPropertyBuilder;

import java.lang.annotation.Annotation;

public class SchemaGenerator {

    public JsonPropertyResult generateSchema(ObjectContext<?> objectContext,
                                             SchemaPropertyContext context) {

        final Annotation[] unwrapAnnotations = context.unwrapAnnotations();
        PropertyBuilder propertyBuilder = new PropertyBuilderDefault(context.propertyCollectors());
        if (unwrapAnnotations.length > 0) {
            propertyBuilder = new UnwrappedPropertyBuilder(propertyBuilder);
        }

        final Property from = propertyBuilder.from(objectContext.getClass());
        return null;
    }
}
