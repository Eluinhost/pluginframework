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
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import junit.framework.AssertionFailedError;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class DefaultCommandRouteTest
{
    @SuppressWarnings("unchecked")
    private Class<? extends CommandSender>[] allSenders = (Class<? extends CommandSender>[]) new Class<?>[]{CommandSender.class};
    @SuppressWarnings("unchecked")
    private Class<? extends CommandSender>[] playerOrConsoleSender = (Class<? extends CommandSender>[]) new Class<?>[]{Player.class, ConsoleCommandSender.class};

    private OptionParser parser;
    private OptionSet set;
    private OptionSpec help;
    private Command command;
    private MethodProxy proxy;

    @Before
    public void onStartup() throws NoSuchMethodException
    {
        parser = mock(OptionParser.class);
        help = mock(OptionSpec.class);
        command = mock(Command.class);
        proxy = mock(MethodProxy.class);
        set = mock(OptionSet.class);

        when(parser.parse(Matchers.<String[]>anyVararg())).thenReturn(set);
        when(set.has(help)).thenReturn(false);
    }

    @Test
    public void test_sender_restricted() throws Throwable
    {
        DefaultCommandRoute route = new DefaultCommandRoute(
                "test",
                new String[]{},
                playerOrConsoleSender,
                proxy,
                parser,
                new String[]{},
                help
        );

        CommandSender sender = mock(BlockCommandSender.class);
        String[] args = new String[]{};

        try {
            route.run(command, sender, args);
        } catch(CommandInvocationException ex) {
            throw ex.getCause();
        }
        verify(parser, never()).parse(args);
        verifyNoMoreInteractions(proxy);
        verify(sender).sendMessage(contains("run that command"));

        sender = mock(Player.class);
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
    public void test_permission_set_default() throws NoSuchMethodException {
        DefaultCommandRoute route = new DefaultCommandRoute("test", new String[]{}, allSenders, proxy, parser, new String[]{}, help);
        assertThat(route.getPermissions()).isEmpty();

        route = new DefaultCommandRoute("test", new String[]{"TEST.PERMISSION"}, allSenders, proxy, parser, new String[]{}, help);
        assertThat(route.getPermissions()).containsExactly("TEST.PERMISSION");
    }

    @Test
    public void test_invalid_permission() throws Throwable
    {
        DefaultCommandRoute route = new DefaultCommandRoute("test", new String[]{"test.permission"}, allSenders, proxy, parser, new String[]{}, help);

        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        when(sender.hasPermission("test.permission")).thenReturn(false);
        String[] args = new String[]{};

        try {
            route.run(command, sender, args);
        } catch(CommandInvocationException ex) {
            throw ex.getCause();
        }
        verifyNoMoreInteractions(proxy);
        verify(sender).sendMessage(anyString());
    }

    @Test
    public void test_valid_invocation() throws Throwable
    {
        DefaultCommandRoute route = new DefaultCommandRoute("test", new String[]{}, allSenders, proxy, parser, new String[]{}, help);

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

        DefaultCommandRoute route = new DefaultCommandRoute("test", new String[]{}, allSenders, proxy, parser, new String[]{}, help);

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
        DefaultCommandRoute route = new DefaultCommandRoute("test", new String[]{"test.permission"}, allSenders, proxy, parser, new String[]{}, help);

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

        DefaultCommandRoute route = new DefaultCommandRoute("test", new String[]{}, allSenders, proxy, parser, new String[]{}, help);

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
        DefaultCommandRoute route = new DefaultCommandRoute("test", new String[]{}, allSenders, proxy, parser, new String[]{}, help);

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
}
