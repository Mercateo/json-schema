package com.mercateo.jsonschema.generictype;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GenericTypeHierarchy {
    public Stream<GenericType<?>> hierarchy(GenericType<?> genericType) {

        Iterable<GenericType<?>> resultIterable = new GenericTypeIterable(genericType);

        return StreamSupport.stream(resultIterable.spliterator(), false);

    }
    private static class GenericTypeIterable implements Iterable<GenericType<?>> {

        private final GenericType<?> genericType;

        GenericTypeIterable(GenericType<?> genericType) {
            this.genericType = genericType;
        }

        @Override
        public Iterator<GenericType<?>> iterator() {
            return new GenericTypeIterator(genericType);
        }
    }

    private static class GenericTypeIterator implements Iterator<GenericType<?>> {
        GenericType<?> currentType;

        GenericTypeIterator(GenericType<?> genericType) {
            this.currentType = genericType;
        }

        @Override
        public boolean hasNext() {
            return currentType != null;
        }

        @Override
        public GenericType<?> next() {
            GenericType<?> type = currentType;
            currentType = currentType.getSuperType();
            return type;
        }

    }
}
