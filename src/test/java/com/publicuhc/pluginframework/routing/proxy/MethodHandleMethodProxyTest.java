/*
 * MethodHandleMethodProxyTest.java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
    public void test_invoking_with_no_arguments() throws Throwable
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
    public void test_invoking_with_arguments() throws Throwable
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
