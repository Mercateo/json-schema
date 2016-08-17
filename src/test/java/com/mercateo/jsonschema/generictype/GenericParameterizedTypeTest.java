package com.mercateo.jsonschema.generictype;

import com.googlecode.gentyref.GenericTypeReflector;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GenericParameterizedTypeTest {

    @Test
    public void testGetContainedType() throws NoSuchFieldException {
        final Field field = TestClass.class.getDeclaredField("doubleList");
        final Type type = GenericTypeReflector.getExactFieldType(field, TestClass.class);

        @SuppressWarnings("rawtypes")
        final GenericParameterizedType<List> genericType = new GenericParameterizedType<>(
                (ParameterizedType) type, List.class);

        final GenericType<?> containedType1 = genericType.getContainedType();
        assertThat(containedType1.getRawType()).isEqualTo(List.class);

        final GenericType<?> containedType2 = containedType1.getContainedType();
        assertThat(containedType2.getRawType()).isEqualTo(Double.class);
    }

    @Test
    public void testGetContainedTypeWithGenericTypeParameter() throws NoSuchFieldException {
        final Class<?> superclass = TestClass.class.getSuperclass();
        final Field field = superclass.getDeclaredField("object");
        final Type type = GenericTypeReflector.getExactFieldType(field, TestClass.class);

        final GenericType<?> genericType = GenericType.of(type);

        assertThat(genericType.getRawType()).isEqualTo(Boolean.class);
    }

    static class SuperClass<T> {
        T object;
    }

    static class TestClass extends SuperClass<Boolean> {
        @SuppressWarnings("unused")
        List<List<Double>> doubleList;

    }

}
