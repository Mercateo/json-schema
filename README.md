# json-schema
[![Build Status](https://travis-ci.org/Mercateo/json-schema.svg?branch=master)](https://travis-ci.org/Mercateo/json-schema)
[![Coverage Status](https://coveralls.io/repos/github/Mercateo/json-schema/badge.svg?branch=master)](https://coveralls.io/github/Mercateo/json-schema?branch=master)
[![Dependency Status](https://www.versioneye.com/user/projects/581e46c089f0a91d99ae3d73/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/581e46c089f0a91d99ae3d73)
[![MavenCentral](https://img.shields.io/maven-central/v/com.mercateo/json-schema.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.mercateo%22%20AND%20a%3A%22json-schema%22)
[![Stories in Ready](https://badge.waffle.io/Mercateo/json-schema.svg?label=ready&title=Ready)](http://waffle.io/Mercateo/json-schema) 

Generic JSON-Schema generator.

## Features

 * Handles Generics
 * Configurable
 * Supports dynamic schema with allowed values and default values
 
## Example

```$java
    SchemaGenerator schemaGenerator = new SchemaGenerator(
            Collections.emptyList(),
            Collections.<RawPropertyCollector>singletonList(new FieldCollector()),
            new HashMap<>());
    
    ObjectNode schema = schemaGenerator.generateSchema(Foo.class);
    
    String schemaString = schema.toString();
```

Generates the following result:
```$javascript
{
  "type": "object", 
  "properties": {
    "baz": {
      "type": "object", 
      "properties": {
        "qux": {
          "type": "string"
        }
      }
    }, 
    "bar": {
      "type": "string"
    }
  }
}}
```
## Property structure

```
class Foo {
    String bar;
    Baz baz;
}

class Baz {
    String qux;
}
```

`bar`, `baz` and `qux` are properties.

### Property representation

Properties are represented as Property Objects specified by containing and contained type:

```
Property<Foo, String> bar;
Property<Foo, Baz> baz;
Property<Baz, String> qux;
```

The part of the property which is represented by its type is contained in a `PropertyDescriptor` which has a generic type representing its contained type.

### Property unwrapping


```
class Foo {
    String bar;
    
    @JsonUnwrapped
    Baz baz;
}

class Baz {
    String qux;
}
```

```
Property<Foo, String> bar;
Property<Foo, String> qux;
```
