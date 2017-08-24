package com.mercateo.jsonschema.property;

import java.util.List;

public class ReferencedPropertyMapperClasses {

    static class PropertyHolder {
        public String property;
    }

    static class RecursivePropertyHolder {
        public List<RecursivePropertyHolder> children;

        public String name;
    }

    static class IdenticalPropertyHolder {
        public PropertyHolder holder1;

        public PropertyHolder holder2;
    }
}
