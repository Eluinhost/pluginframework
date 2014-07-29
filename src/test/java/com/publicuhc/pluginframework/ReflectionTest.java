package com.publicuhc.pluginframework;

import com.publicuhc.pluginframework.routing.DefaultMethodProxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.invoke.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(PowerMockRunner.class)
public class ReflectionTest
{
    private TestObject instance;

    @Before
    public void onStartup()
    {
        instance = new TestObject();
    }

    @Test
    public void testInvokeNoArg() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
    {
        Method method = TestObject.class.getMethod("getNoArg");
        DefaultMethodProxy proxy = new DefaultMethodProxy(instance, method);

        Object returns = proxy.invoke();

        assertThat(returns).isInstanceOf(String.class);
        assertThat(returns).isSameAs(TestObject.TEST_STRING);
    }

    @Test
    public void testInvokeWithArg() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method method = TestObject.class.getMethod("getArg", String.class);
        DefaultMethodProxy proxy = new DefaultMethodProxy(instance, method);

        Object returns = proxy.invoke("test");

        assertThat(returns).isInstanceOf(String.class);
        assertThat(returns).isSameAs("test");
    }

    @Test
    public void testSpeedRegular()
    {
        long start = System.currentTimeMillis();
        for(int i = 0; i < 1000000; i++) {
            instance.getArg("test");
        }
        System.out.println("Regular object, 1 Million invocations: " + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void testSpeedReflection() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        long start = System.currentTimeMillis();
        for(int i = 0; i < 1000000; i++) {
            Method method = TestObject.class.getMethod("getArg", String.class);
            method.invoke(instance, "test");
        }
        System.out.println("Reflection, 1 Million invocations: " + (System.currentTimeMillis() - start) + "ms");

    }

    @Test
    public void testSpeedReflectionWithCache() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method method = TestObject.class.getMethod("getArg", String.class);

        DefaultMethodProxy proxy = new DefaultMethodProxy(instance, method);
        long start = System.currentTimeMillis();
        for(int i = 0; i < 1000000; i++) {
            proxy.invoke("test");
        }
        System.out.println("Reflection with cache, 1 Million invocations: " + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void testSpeedInvokeDynamic() throws Throwable
    {
        MethodHandle handle = MethodHandles.lookup().findVirtual(TestObject.class, "getArg", MethodType.methodType(String.class, String.class));

        long start = System.currentTimeMillis();
        for(int i = 0; i < 1000000; i++) {
            //need to store result or things go crazy
            String result = (String) handle.invokeExact(instance, "test");
        }
        System.out.println("invokedynamic, 1 Million invocations: " + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void testSpeedInvokeDynamicCallsite() throws Throwable
    {
        MethodHandle handle = MethodHandles.lookup().findVirtual(TestObject.class, "getArg", MethodType.methodType(String.class, String.class));
        CallSite site = new ConstantCallSite(handle);

        long start = System.currentTimeMillis();
        for(int i = 0; i < 1000000; i++) {
            //need to get the result or things go crazy
            String result = (String) site.dynamicInvoker().invokeExact(instance, "test");
        }
        System.out.println("invokedynamic with callsite, 1 Million invocations: " + (System.currentTimeMillis() - start) + "ms");
    }

    public class TestObject
    {
        public static final String TEST_STRING = "TEST STRING";

        public String getNoArg()
        {
            return TEST_STRING;
        }

        public String getArg(String arg)
        {
            return arg;
        }
    }
}