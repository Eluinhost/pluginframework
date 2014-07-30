package com.publicuhc.pluginframework.benchmarks;

import com.publicuhc.pluginframework.TestObject;
import com.publicuhc.pluginframework.routing.proxy.ReflectionMethodProxy;
import org.openjdk.jmh.annotations.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ReflectionMethodProxyBenchmarks
{
    private TestObject instance;
    private ReflectionMethodProxy<TestObject> argProxy;
    private ReflectionMethodProxy<TestObject> noArgProxy;

    //store here to stop constant folding
    private String arg = "test";

    @Setup
    public void startup() throws NoSuchMethodException, IllegalAccessException
    {
        instance = new TestObject();
        Method argMethod = TestObject.class.getDeclaredMethod("getArg", String.class);
        argProxy = new ReflectionMethodProxy<TestObject>(instance, argMethod);

        Method noArgMethod = TestObject.class.getDeclaredMethod("getNoArg");
        noArgProxy = new ReflectionMethodProxy<TestObject>(instance, noArgMethod);
    }

    @Benchmark
    public String returnWithArg() throws InvocationTargetException, IllegalAccessException
    {
        return (String) argProxy.invoke(arg);
    }

    @Benchmark
    public String returnWithNoArg() throws InvocationTargetException, IllegalAccessException
    {
        return (String) noArgProxy.invoke();
    }
}
