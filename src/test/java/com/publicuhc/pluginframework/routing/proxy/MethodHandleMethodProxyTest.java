/*
 * MethodHandleMethodProxyTest.java
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
