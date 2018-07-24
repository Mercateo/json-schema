# json-schema
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/93fedefc8591476d84aa9e3b76ad5f53)](https://app.codacy.com/app/wuan/json-schema?utm_source=github.com&utm_medium=referral&utm_content=Mercateo/json-schema&utm_campaign=badger)
[![Build Status](https://travis-ci.org/Mercateo/json-schema.svg?branch=master)](https://travis-ci.org/Mercateo/json-schema)
[![Coverage Status](https://coveralls.io/repos/github/Mercateo/json-schema/badge.svg?branch=master)](https://coveralls.io/github/Mercateo/json-schema?branch=master)
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

# Benchmarks

current results

```
Benchmark                         Mode  Cnt        Score        Error  Units
Benchmarks.createRepeatedSchema  thrpt   10  1847783.024 ± 366533.953  ops/s
Benchmarks.createSchema          thrpt   10   234140.670 ±  34461.239  ops/s
```

`createSchema` creates `SchemaGenerator` and schema for each iteration, `createdRepeatedSchema` uses caching in the `SchemaGenerator` for schema creation
