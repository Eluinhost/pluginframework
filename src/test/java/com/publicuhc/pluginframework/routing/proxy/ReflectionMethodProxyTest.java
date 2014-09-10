/*
 * ReflectionMethodProxyTest.java
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
public class ReflectionMethodProxyTest
{
    @Test
    public void test_invoking_without_arguments() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
    {
        TestReflectedObject object = new TestReflectedObject();
        Method method = TestReflectedObject.class.getMethod("testNoArg");
        ReflectionMethodProxy proxy = new ReflectionMethodProxy(object, method);
        proxy.invoke();
    }

    @Test
    public void test_invoking_with_arguments() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        TestReflectedObject object = new TestReflectedObject();
        Method method = TestReflectedObject.class.getMethod("testCorrect", String.class, Double.class, Integer[].class);
        ReflectionMethodProxy proxy = new ReflectionMethodProxy(object, method);

        proxy.invoke("test", 10.2D, new Integer[]{1, 2, 3});
    }

    @Test
    public void test_with_return() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        TestReflectedObject object = new TestReflectedObject();
        String mocked = mock(String.class);
        Method method = TestReflectedObject.class.getMethod("testReturn", String.class);
        ReflectionMethodProxy proxy = new ReflectionMethodProxy(object, method);

        assertThat(proxy.invoke(mocked)).isSameAs(mocked);
    }

    @Test
    public void test_invoke_with_arguments_array() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        TestReflectedObject object = new TestReflectedObject();
        Method method = TestReflectedObject.class.getMethod("testCorrect", String.class, Double.class, Integer[].class);
        ReflectionMethodProxy proxy = new ReflectionMethodProxy(object, method);

        Object[] args = new Object[]{"test", 10.2D, new Integer[]{1, 2, 3}};

        proxy.invoke(args);
    }
}
