package com.mercateo.jsonschema.schema;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchemaGenerator {

    public JsonPropertyResult generateSchema(ObjectContext<?> objectContext,
                                               SchemaPropertyContext context) {
        return null;
    }
}
