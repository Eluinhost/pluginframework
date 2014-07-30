package com.publicuhc.pluginframework.routing.proxy;

import com.publicuhc.pluginframework.TestObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(PowerMockRunner.class)
public class ReflectionMethodProxyTest
{
    @Test
    public void testInvokeNoArg() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
    {
        Method method = TestObject.class.getMethod("getNoArg");
        ReflectionMethodProxy proxy = new ReflectionMethodProxy(new TestObject(), method);

        Object returns = proxy.invoke();

        assertThat(returns).isInstanceOf(String.class);
        assertThat(returns).isSameAs(TestObject.TEST_STRING);
    }

    @Test
    public void testInvokeWithArg() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method method = TestObject.class.getMethod("getArg", String.class);
        ReflectionMethodProxy proxy = new ReflectionMethodProxy(new TestObject(), method);

        Object returns = proxy.invoke(TestObject.TEST_STRING);

        assertThat(returns).isInstanceOf(String.class);
        assertThat(returns).isSameAs(TestObject.TEST_STRING);
    }
}
