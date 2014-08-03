/*
 * DefaultCommandRouteTest.java
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

package com.publicuhc.pluginframework.routing;

import com.publicuhc.pluginframework.routing.exception.CommandInvocationException;
import com.publicuhc.pluginframework.routing.proxy.MethodProxy;
import com.publicuhc.pluginframework.routing.proxy.ReflectionMethodProxy;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import junit.framework.AssertionFailedError;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
public class DefaultCommandRouteTest
{
    private OptionParser parser;
    private TestClass testObject;

    @Before
    public void onStartup() throws NoSuchMethodException
    {
        testObject = new TestClass();
        parser = mock(OptionParser.class);
        OptionSet set = mock(OptionSet.class);
        when(parser.parse(Matchers.<String[]>anyVararg())).thenReturn(set);
        List nonOptions = new ArrayList<String>();
        nonOptions.add("a");
        nonOptions.add("abc");
        when(set.nonOptionArguments()).thenReturn(nonOptions);
    }

    @Test
    public void test_permission_set_default() throws NoSuchMethodException {
        Method method = TestClass.class.getMethod("testMethod", CommandRequest.class);
        MethodProxy proxy = spy(new ReflectionMethodProxy(testObject, method));

        DefaultCommandRoute route = new DefaultCommandRoute("test", CommandMethod.NO_PERMISSIONS, proxy, parser);
        assertThat(route.getPermission()).isNull();

        route = new DefaultCommandRoute("test", "TEST.PERMISSION", proxy, parser);
        assertThat(route.getPermission()).isEqualTo("TEST.PERMISSION");
    }

    @Test
    public void test_valid_invocation() throws Throwable
    {
        Method method = TestClass.class.getMethod("testMethod", CommandRequest.class);
        MethodProxy proxy = spy(new ReflectionMethodProxy(testObject, method));
        DefaultCommandRoute route = new DefaultCommandRoute("test", CommandMethod.NO_PERMISSIONS, proxy, parser);

        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = new String[]{"1", "2"};

        try {
            route.run(command, sender, args);
        } catch(CommandInvocationException ex) {
            throw ex.getCause();
        }
        verify(parser, times(1)).parse(args);
        verify(proxy, times(1)).invoke(any(CommandRequest.class));

        assertThat(testObject.wasRan()).isTrue();
    }

    @Test
    public void test_invocation_with_exception() throws Throwable
    {
        Method method = TestClass.class.getMethod("exceptionMethod", CommandRequest.class);
        MethodProxy proxy = spy(new ReflectionMethodProxy(testObject, method));
        DefaultCommandRoute route = new DefaultCommandRoute("test", CommandMethod.NO_PERMISSIONS, proxy, parser);

        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = new String[]{"1", "2"};

        try {
            route.run(command, sender, args);
            throw new AssertionFailedError("Expected CommandInvocationException");
        } catch(CommandInvocationException ignored) {
            verify(parser, times(1)).parse(args);
            verify(proxy, times(1)).invoke(any(CommandRequest.class));
            assertThat(testObject.wasRan()).isFalse();
        }
    }

    public static class TestClass
    {
        private boolean ran = false;

        public void testMethod(CommandRequest request)
        {
            setRan(true);
        }

        public void exceptionMethod(CommandRequest request)
        {
            throw new IllegalStateException();
        }

        public void setRan(boolean ran)
        {
            this.ran = ran;
        }

        public boolean wasRan()
        {
            return ran;
        }
    }
}
