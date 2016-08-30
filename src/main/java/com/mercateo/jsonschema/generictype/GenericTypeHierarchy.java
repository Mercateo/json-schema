package com.mercateo.jsonschema.generictype;

import javaslang.collection.Iterator;
import javaslang.collection.Stream;

public final class GenericTypeHierarchy {
    public Stream<GenericType<?>> hierarchy(GenericType<?> genericType) {
        return Stream.ofAll(new GenericTypeIterable(genericType));
    }

    private final static class GenericTypeIterable implements Iterable<GenericType<?>> {

        private final GenericType<?> genericType;

        GenericTypeIterable(GenericType<?> genericType) {
            this.genericType = genericType;
        }

        @Override
        public Iterator<GenericType<?>> iterator() {
            return new GenericTypeIterator(genericType);
        }
    }

    private final static class GenericTypeIterator implements Iterator<GenericType<?>> {
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
