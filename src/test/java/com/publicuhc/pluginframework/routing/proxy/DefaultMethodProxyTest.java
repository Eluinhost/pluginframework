package com.publicuhc.pluginframework.routing.proxy;

import com.publicuhc.pluginframework.TestObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(PowerMockRunner.class)
public class DefaultMethodProxyTest
{
    @Test
    public void testInvokeNoArg() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
    {
        Method method = TestObject.class.getMethod("getNoArg");
        DefaultMethodProxy proxy = new DefaultMethodProxy(new TestObject(), method);

        Object returns = proxy.invoke();

        assertThat(returns).isInstanceOf(String.class);
        assertThat(returns).isSameAs(TestObject.TEST_STRING);
    }

    @Test
    public void testInvokeWithArg() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method method = TestObject.class.getMethod("getArg", String.class);
        DefaultMethodProxy proxy = new DefaultMethodProxy(new TestObject(), method);

        Object returns = proxy.invoke("test");

        assertThat(returns).isInstanceOf(String.class);
        assertThat(returns).isSameAs("test");
    }
}
