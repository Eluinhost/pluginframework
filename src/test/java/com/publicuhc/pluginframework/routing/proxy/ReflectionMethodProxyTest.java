/*
 * ReflectionMethodProxyTest.java
 *
 * Copyright (c) 2014. Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of PluginFramework.
 *
 * PluginFramework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PluginFramework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PluginFramework.  If not, see <http ://www.gnu.org/licenses/>.
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
