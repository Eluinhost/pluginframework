package com.publicuhc.pluginframework.routing.proxy;

import com.publicuhc.pluginframework.TestObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
public class MethodHandleMethodProxyTest
{
    @Test
    public void testInvokeNoArg() throws Throwable
    {
        TestObject object = spy(new TestObject());
        MethodHandle method = MethodHandles.lookup().findVirtual(TestObject.class, "getNoArg", MethodType.methodType(String.class));
        MethodHandleMethodProxy proxy = new MethodHandleMethodProxy<TestObject>(object, method);

        String returns = (String) proxy.invoke();

        assertThat(returns).isInstanceOf(String.class);
        assertThat(returns).isSameAs(TestObject.TEST_STRING);
        verify(object, times(1)).getNoArg();
        verifyNoMoreInteractions(object);
    }

    @Test
    public void testInvokeWithArg() throws Throwable
    {
        TestObject object = spy(new TestObject());
        MethodHandle method = MethodHandles.lookup().findVirtual(TestObject.class, "getArg", MethodType.methodType(String.class, String.class));
        MethodHandleMethodProxy proxy = new MethodHandleMethodProxy<TestObject>(object, method);

        String returns = (String) proxy.invoke(TestObject.TEST_STRING);

        assertThat(returns).isInstanceOf(String.class);
        assertThat(returns).isSameAs(TestObject.TEST_STRING);
        verify(object, times(1)).getArg(TestObject.TEST_STRING);
        verifyNoMoreInteractions(object);
    }
}
