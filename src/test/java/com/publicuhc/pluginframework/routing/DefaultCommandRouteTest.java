/*
 * DefaultCommandRouteTest.java
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

package com.publicuhc.pluginframework.routing;

import com.publicuhc.pluginframework.WithSelfAnswer;
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
import mkremins.fanciful.FancyMessage;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DefaultCommandRoute.class)
public class DefaultCommandRouteTest
{
    private OptionParser parser;
    private OptionSet set;
    private OptionSpec help;
    private Command command;
    private MethodProxy proxy;
    private List<CommandTester> defaultTesters;

    @Before
    public void onStartup() throws Exception
    {
        parser = mock(OptionParser.class);
        help = mock(OptionSpec.class);
        when(help.options()).thenReturn(Arrays.asList("?"));
        FancyMessage mockedMessage = mock(FancyMessage.class, new WithSelfAnswer(FancyMessage.class));
        whenNew(FancyMessage.class).withAnyArguments().thenReturn(mockedMessage);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                ((CommandSender) invocation.getArguments()[0]).sendMessage("");
                return null;
            }
        }).when(mockedMessage).send(any(CommandSender.class));
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
