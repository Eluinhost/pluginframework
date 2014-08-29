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
import com.publicuhc.pluginframework.routing.tester.CommandTester;
import com.publicuhc.pluginframework.routing.tester.PermissionTester;
import com.publicuhc.pluginframework.routing.tester.SenderTester;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import junit.framework.AssertionFailedError;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class DefaultCommandRouteTest
{
    private OptionParser parser;
    private OptionSet set;
    private OptionSpec help;
    private Command command;
    private MethodProxy proxy;
    private List<CommandTester> defaultTesters;

    @Before
    public void onStartup() throws NoSuchMethodException
    {
        parser = mock(OptionParser.class);
        help = mock(OptionSpec.class);
        command = mock(Command.class);
        proxy = mock(MethodProxy.class);
        set = mock(OptionSet.class);
        defaultTesters = new ArrayList<CommandTester>();

        when(parser.parse(Matchers.<String[]>anyVararg())).thenReturn(set);
        when(set.has(help)).thenReturn(false);
    }

    @Test
    public void test_sender_restricted() throws Throwable
    {
        SenderTester tester = mock(SenderTester.class);
        when(tester.testCommand(eq(command), any(CommandSender.class), any(String[].class))).thenReturn(false);
        defaultTesters.add(tester);

        DefaultCommandRoute route = new DefaultCommandRoute(
                "test",
                proxy,
                parser,
                new String[]{},
                help,
                defaultTesters
        );

        CommandSender sender = mock(BlockCommandSender.class);
        String[] args = new String[]{};

        try {
            route.run(command, sender, args);
        } catch(CommandInvocationException ex) {
            throw ex.getCause();
        }
        verify(tester, times(1)).testCommand(command, sender, args);
        verifyNoMoreInteractions(proxy);

        when(tester.testCommand(command, sender, args)).thenReturn(true);

        try {
            route.run(command, sender, args);
        } catch(CommandInvocationException ex) {
            throw ex.getCause();
        }
        verify(parser, times(1)).parse(args);
        verify(proxy, times(1)).invoke(set, sender);
        verify(sender, never()).sendMessage(anyString());
    }

    @Test
    public void test_invalid_permission() throws Throwable
    {
        PermissionTester tester = mock(PermissionTester.class);
        when(tester.testCommand(eq(command), any(CommandSender.class), any(String[].class))).thenReturn(false);
        defaultTesters.add(tester);

        DefaultCommandRoute route = new DefaultCommandRoute("test", proxy, parser, new String[]{}, help, defaultTesters);

        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = new String[]{};

        try {
            route.run(command, sender, args);
        } catch(CommandInvocationException ex) {
            throw ex.getCause();
        }
        verifyNoMoreInteractions(proxy);
        verify(tester).testCommand(command, sender, args);
    }

    @Test
    public void test_valid_invocation() throws Throwable
    {
        DefaultCommandRoute route = new DefaultCommandRoute("test", proxy, parser, new String[]{}, help, defaultTesters);

        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = new String[]{"1", "2"};

        try {
            route.run(command, sender, args);
        } catch(CommandInvocationException ex) {
            throw ex.getCause();
        }
        verify(parser, times(1)).parse(args);
        verify(proxy, times(1)).invoke(set, sender);
    }

    @Test
    public void test_help_print_option() throws Throwable
    {
        when(set.has(help)).thenReturn(true);

        DefaultCommandRoute route = new DefaultCommandRoute("test", proxy, parser, new String[]{}, help, defaultTesters);

        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = new String[]{"-?"};

        try {
            route.run(command, sender, args);
        } catch(CommandInvocationException ex) {
            throw ex.getCause();
        }
        verify(parser, times(1)).parse(args);
        verifyNoMoreInteractions(proxy);
        verify(sender).sendMessage(anyString());
    }



    @Test
    public void test_valid_permission() throws Throwable
    {
        DefaultCommandRoute route = new DefaultCommandRoute("test", proxy, parser, new String[]{}, help, defaultTesters);

        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        when(sender.hasPermission("test.permission")).thenReturn(true);
        String[] args = new String[]{};

        try {
            route.run(command, sender, args);
        } catch(CommandInvocationException ex) {
            throw ex.getCause();
        }
        verify(proxy, times(1)).invoke(set, sender);
        verify(sender, never()).sendMessage(anyString());
    }

    @Test
    public void test_help_print_invalid_options() throws Throwable
    {
        when(parser.parse(Matchers.<String[]>anyVararg())).thenThrow(mock(OptionException.class));

        DefaultCommandRoute route = new DefaultCommandRoute("test", proxy, parser, new String[]{}, help, defaultTesters);

        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = new String[]{"1", "2"};

        try {
            route.run(command, sender, args);
        } catch(CommandInvocationException ex) {
            throw ex.getCause();
        }
        verify(parser, times(1)).parse(args);
        verifyNoMoreInteractions(proxy);
        verify(sender).sendMessage(anyString());
    }

    @Test
    public void test_invocation_with_exception() throws Throwable
    {
        DefaultCommandRoute route = new DefaultCommandRoute("test", proxy, parser, new String[]{}, help, defaultTesters);

        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = new String[]{"1", "2"};

        //noinspection unchecked
        when(proxy.invoke(set, sender)).thenThrow(CommandInvocationException.class);

        try {
            route.run(command, sender, args);
            throw new AssertionFailedError("Expected CommandInvocationException");
        } catch(CommandInvocationException ignored) {
            verify(parser, times(1)).parse(args);
            verify(proxy, times(1)).invoke(set, sender);
        }
    }

    @Test
    public void test_fix_quotes()
    {
        DefaultCommandRoute route = new DefaultCommandRoute("test", proxy, parser, new String[]{}, help, defaultTesters);

        String[] testArgs = new String[]{
                "separate",
                "args",
                "\"this",
                "is",
                "in",
                "one",
                "arg\"",
                "outer",
                "stuff"
        };

        String[] fixed = route.fixQuoted(testArgs);
        assertThat(fixed).containsExactly("separate", "args", "this is in one arg", "outer", "stuff");

        testArgs = new String[]{
                "th\"ese",
                "arg\"s",
                "shouldn't\"",
                "be",
                "affected"
        };
        fixed = route.fixQuoted(testArgs);
        assertThat(fixed).containsExactly("th\"ese", "arg\"s", "shouldn't\"", "be", "affected");

        testArgs = new String[] {
                "\"these",
                "args",
                "are",
                "all",
                "in",
                "one"
        };
        fixed = route.fixQuoted(testArgs);
        assertThat(fixed).containsExactly("these args are all in one");
    }
}
