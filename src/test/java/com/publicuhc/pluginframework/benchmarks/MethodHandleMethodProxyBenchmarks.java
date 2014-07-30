package com.publicuhc.pluginframework.benchmarks;

import com.publicuhc.pluginframework.TestObject;
import com.publicuhc.pluginframework.routing.proxy.MethodHandleMethodProxy;
import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MethodHandleMethodProxyBenchmarks
{
    private MethodHandleMethodProxy<TestObject> argProxy;
    private MethodHandleMethodProxy<TestObject> noArgProxy;

    //store here to stop constant folding
    private String arg = "test";

    @Setup
    public void startup() throws NoSuchMethodException, IllegalAccessException
    {
        TestObject instance = new TestObject();

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle noArgHandle = lookup.findVirtual(TestObject.class, "getNoArg", MethodType.methodType(String.class));
        MethodHandle argHandle = lookup.findVirtual(TestObject.class, "getArg", MethodType.methodType(String.class, String.class));

        argProxy = new MethodHandleMethodProxy<TestObject>(instance, argHandle);
        noArgProxy = new MethodHandleMethodProxy<TestObject>(instance, noArgHandle);
    }

    @Benchmark
    public String returnWithArg() throws Throwable
    {
        return (String) argProxy.invoke(arg);
    }

    @Benchmark
    public String returnWithNoArg() throws Throwable
    {
        return (String) noArgProxy.invoke();
    }
}
