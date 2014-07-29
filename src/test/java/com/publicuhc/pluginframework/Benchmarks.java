package com.publicuhc.pluginframework;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Benchmarks
{
    public static void main(String[] args) throws RunnerException
    {
        Options opt = new OptionsBuilder()
                .include(".*")
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(3)
                .build();

        new Runner(opt).run();
    }
}
