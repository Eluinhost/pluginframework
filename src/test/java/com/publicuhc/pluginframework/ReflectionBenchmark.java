package com.publicuhc.pluginframework;

import org.openjdk.jmh.annotations.*;

import java.lang.invoke.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ReflectionBenchmark
{
    private TestObject instance;
    private Method method;
    private MethodHandle handle;
    private CallSite site;

    //store here to stop constant folding
    private String arg = "test";

    @Setup
    public void startup() throws NoSuchMethodException, IllegalAccessException
    {
        instance = new TestObject();
        method = TestObject.class.getMethod("getArg", String.class);
        handle = MethodHandles.publicLookup().findVirtual(TestObject.class, "getArg", MethodType.methodType(String.class, String.class));
        site = new ConstantCallSite(handle);
    }

    @Benchmark
    public String reflection() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        return (String) method.invoke(instance, arg);
    }

    @Benchmark
    public String direct()
    {
        return instance.getArg(arg);
    }

    @Benchmark
    public String invokeDynamic() throws Throwable
    {
        //need to cast result or things go crazy
        return (String) handle.invokeExact(instance, arg);
    }

    @Benchmark
    public String invokeDynamicCallsite() throws Throwable
    {
        return (String) site.dynamicInvoker().invokeExact(instance, arg);
    }
}
