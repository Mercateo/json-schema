package com.mercateo.jsonschema.mapper

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode

class ObjectNodeFactory {

    fun createNode(): ObjectNode {
        return ObjectNode(nodeFactory)
    }

    companion object {
        val nodeFactory = JsonNodeFactory(true)
    }
}
