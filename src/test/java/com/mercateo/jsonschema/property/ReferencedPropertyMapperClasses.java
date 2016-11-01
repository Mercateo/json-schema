package com.mercateo.jsonschema.property;

import java.util.List;

public class ReferencedPropertyMapperClasses {

    static class PropertyHolder {
        String property;
    }

    static class RecursivePropertyHolder {
        List<RecursivePropertyHolder> children;

        String name;
    }

    static class IdenticalPropertyHolder {
        PropertyHolder holder1;

        PropertyHolder holder2;
    }
}
