package com.publicuhc.pluginframework.routing.proxy;

import com.publicuhc.pluginframework.TestObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
public class ReflectionMethodProxyTest
{
    @Test
    public void test_invoking_without_arguments() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
    {
        TestObject object = spy(new TestObject());
        Method method = TestObject.class.getMethod("getNoArg");
        ReflectionMethodProxy proxy = new ReflectionMethodProxy(object, method);

        Object returns = proxy.invoke();

        assertThat(returns).isInstanceOf(String.class);
        assertThat(returns).isSameAs(TestObject.TEST_STRING);
        verify(object, times(1)).getNoArg();
        verifyNoMoreInteractions(object);
    }

    @Test
    public void test_invoking_with_arguments() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        TestObject object = spy(new TestObject());
        Method method = TestObject.class.getMethod("getArg", String.class);
        ReflectionMethodProxy proxy = new ReflectionMethodProxy(object, method);

        Object returns = proxy.invoke(TestObject.TEST_STRING);

        assertThat(returns).isInstanceOf(String.class);
        assertThat(returns).isSameAs(TestObject.TEST_STRING);
        verify(object, times(1)).getArg(TestObject.TEST_STRING);
        verifyNoMoreInteractions(object);
    }
}
