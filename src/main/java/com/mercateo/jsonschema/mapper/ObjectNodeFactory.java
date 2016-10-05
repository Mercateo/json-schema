package com.mercateo.jsonschema.mapper;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ObjectNodeFactory {

    public static final JsonNodeFactory nodeFactory = new JsonNodeFactory(true);

    public ObjectNode createNode() {
        return new ObjectNode(nodeFactory);
    }
}
