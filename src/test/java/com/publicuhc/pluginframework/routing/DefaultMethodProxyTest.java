package com.publicuhc.pluginframework.routing;

import oracle.jrockit.jfr.StringConstantPool;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(PowerMockRunner.class)
public class DefaultMethodProxyTest {

    private TestObject instance;

    @Before
    public void onStartup()
    {
        instance = new TestObject();
    }

    @Test
    public void testInvokeNoArg() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = TestObject.class.getMethod("getNoArg");
        DefaultMethodProxy proxy = new DefaultMethodProxy(instance, method);

        Object returns = proxy.invoke();

        assertThat(returns).isInstanceOf(String.class);
        assertThat(returns).isSameAs(TestObject.TEST_STRING);
    }

    @Test
    public void testInvokeWithArg() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = TestObject.class.getMethod("getArg", String.class);
        DefaultMethodProxy proxy = new DefaultMethodProxy(instance, method);

        Object returns = proxy.invoke("test");

        assertThat(returns).isInstanceOf(String.class);
        assertThat(returns).isSameAs("test");
    }

    @Test
    public void testSpeedRegular()
    {
        TestObject object = new TestObject();
        long start = System.currentTimeMillis();
        for(int i = 0; i<1000000; i++) {
            object.getArg("test");
        }
        System.out.println("Regular object, 1 Million invocations: " + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void testSpeedReflection() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        TestObject object = new TestObject();

        long start = System.currentTimeMillis();
        for(int i = 0; i<1000000; i++) {
            Method method = TestObject.class.getMethod("getArg", String.class);
            method.invoke(object, "test");
        }
        System.out.println("Reflection, 1 Million invocations: " + (System.currentTimeMillis() - start) + "ms");

    }

    @Test
    public void testSpeedReflectionWithCache() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        TestObject object = new TestObject();
        Method method = TestObject.class.getMethod("getArg", String.class);

        DefaultMethodProxy proxy = new DefaultMethodProxy(object, method);
        long start = System.currentTimeMillis();
        for(int i = 0; i<1000000; i++) {
            proxy.invoke("test");
        }
        System.out.println("Reflection with cache, 1 Million invocations: " + (System.currentTimeMillis() - start) + "ms");
    }

    private class TestObject {
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
