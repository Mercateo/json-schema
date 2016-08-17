package com.mercateo.jsonschema.schema;

import com.mercateo.jsonschema.property.Property;

import java.util.function.Predicate;

/**
 * this class checks, if a field of a bean should be contained in the schema
 *
 * @author joerg_adler
 */
public interface PropertyChecker extends Predicate<Property> {
    static PropertyChecker fromPredicate(Predicate<Property> predicate) {
        return predicate::test;
    }
}
