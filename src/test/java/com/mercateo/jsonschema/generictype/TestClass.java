package com.mercateo.jsonschema.generictype;

import java.util.List;

public class TestClass extends SuperClass<Boolean> {
    @SuppressWarnings("unused")
    List<List<Double>> doubleList;

    Float[] floatArray;

    float[] primitiveFloatArray;
}
