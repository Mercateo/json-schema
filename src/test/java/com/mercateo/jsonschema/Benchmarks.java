package com.mercateo.jsonschema;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.jsonschema.schema.SchemaGenerator;
import com.mercateo.jsonschema.schema.SchemaGeneratorClasses;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Benchmarks {

    private static final SchemaGenerator schemaGenerator = new SchemaGenerator();

    @Benchmark
    public static void createRepeatedSchema() {
        final ObjectNode nodes = schemaGenerator.generateSchema(SchemaGeneratorClasses.Simple.class);
        final String schemaString = nodes.toString();
    }

    @Benchmark
    public static void createSchema() {
        final SchemaGenerator schemaGenerator = new SchemaGenerator();

        final ObjectNode nodes = schemaGenerator.generateSchema(SchemaGeneratorClasses.Simple.class);
        final String schemaString = nodes.toString();
    }

    public static void main(String[] args) throws RunnerException, InterruptedException {

        Options opt = new OptionsBuilder()
                .warmupIterations(10)
                .measurementIterations(10)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
