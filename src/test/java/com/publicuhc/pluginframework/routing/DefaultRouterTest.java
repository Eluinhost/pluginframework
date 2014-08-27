/*
 * DefaultRouterTest.java
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

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.publicuhc.pluginframework.routing.exception.CommandParseException;
import com.publicuhc.pluginframework.routing.parser.DefaultRoutingMethodParser;
import com.publicuhc.pluginframework.routing.testcommands.InvalidCommand;
import com.publicuhc.pluginframework.routing.testcommands.SampleCommand;
import com.publicuhc.pluginframework.routing.testcommands.SampleSubcommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.publicuhc.pluginframework.matchers.UHCMatchers.listOfSize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, PluginCommand.class})
public class DefaultRouterTest
{
    private DefaultRouter router;
    private Injector injector;
    private Injector childInjector;

    @Before
    public void onStartup()
    {
        injector = getMockInjector();
        childInjector = getMockInjector();

        when(injector.createChildInjector(anyListOf(AbstractModule.class))).thenReturn(childInjector);

        router = new DefaultRouter(new DefaultRoutingMethodParser(), injector, mock(PluginLogger.class));

        PluginCommand command = mock(PluginCommand.class);
        when(command.getName()).thenReturn("test");

        mockStatic(Bukkit.class);
        when(Bukkit.getPluginCommand("test")).thenReturn(command);
    }

    private Injector getMockInjector()
    {
        Injector i = mock(Injector.class);
        when(i.getInstance(InvalidCommand.class)).thenReturn(new InvalidCommand());
        when(i.getInstance(SampleCommand.class)).thenReturn(new SampleCommand());
        when(i.getInstance(SampleSubcommand.class)).thenReturn(new SampleSubcommand());
        return i;
    }

    @Test
    public void test_register_command_by_class_no_modules() throws Throwable
    {
        router.registerCommands(SampleCommand.class);
        assertThat(router.commands).hasSize(1);
        assertThat(router.commands.get("test")).hasSize(1);

        //it should create a child injector and use that to create the class
        verify(injector, times(1)).createChildInjector(listOfSize(0));
        verifyNoMoreInteractions(injector);
        verify(childInjector, times(1)).getInstance(SampleCommand.class);
        verify(childInjector, never()).injectMembers(any(SampleCommand.class));
        verifyNoMoreInteractions(childInjector);
    }

    @Test
    public void test_register_command_by_class_extra_modules() throws Throwable
    {
        List<AbstractModule> modules = new ArrayList<AbstractModule>();
        router.registerCommands(SampleCommand.class, modules);
        assertThat(router.commands).hasSize(1);
        assertThat(router.commands.get("test")).hasSize(1);

        //it should create a child injector with given modules and use that to create the class
        verify(injector, times(1)).createChildInjector(modules);
        verifyNoMoreInteractions(injector);
        verify(childInjector, times(1)).getInstance(SampleCommand.class);

        //we're using the class so it shouldnt inject
        verify(childInjector, never()).injectMembers(any(SampleCommand.class));
        verifyNoMoreInteractions(childInjector);
    }

    @Test
    public void test_register_command_by_instance_inject_no_modules() throws Throwable
    {
        router.registerCommands(new SampleCommand(), true);
        assertThat(router.commands).hasSize(1);
        assertThat(router.commands.get("test")).hasSize(1);

        //it should create a child injector and use that to inject, not creating a class
        verify(injector, times(1)).createChildInjector(listOfSize(0));
        verifyNoMoreInteractions(injector);
        //we don't get an instance
        verify(childInjector, never()).getInstance(SampleCommand.class);
        //we inject instead
        verify(childInjector, times(1)).injectMembers(any(SampleCommand.class));
        verifyNoMoreInteractions(childInjector);
    }

    @Test
    public void test_register_command_by_instance_inject_extra_modules() throws Throwable
    {
        List<AbstractModule> modules = new ArrayList<AbstractModule>();
        router.registerCommands(new SampleCommand(), true, modules);
        assertThat(router.commands).hasSize(1);
        assertThat(router.commands.get("test")).hasSize(1);

        //it should create a child injector and use that to inject, not creating a class
        verify(injector, times(1)).createChildInjector(modules);
        verifyNoMoreInteractions(injector);
        verify(childInjector, never()).getInstance(SampleCommand.class);
        verify(childInjector, times(1)).injectMembers(any(SampleCommand.class));
        verifyNoMoreInteractions(childInjector);
    }


    @Test
    public void test_register_command_by_instance_no_inject_no_modules() throws Throwable
    {
        router.registerCommands(new SampleCommand(), false);
        assertThat(router.commands).hasSize(1);
        assertThat(router.commands.get("test")).hasSize(1);

        //it shouldn't do anything with the injector at all
        verifyNoMoreInteractions(injector);
        verifyNoMoreInteractions(childInjector);
    }

    @Test
    public void test_register_command_by_instance_no_inject_extra_modules() throws Throwable
    {
        router.registerCommands(new SampleCommand(), false, new ArrayList<AbstractModule>());
        assertThat(router.commands).hasSize(1);
        assertThat(router.commands.get("test")).hasSize(1);

        //it shouldn't do anything with the injector at all even though we gave it extra modules
        verifyNoMoreInteractions(injector);
        verifyNoMoreInteractions(childInjector);
    }

    @Test(expected = CommandParseException.class)
    public void test_invalid_command_register() throws Throwable
    {
        router.registerCommands(new InvalidCommand(), true);
    }

    @Test
    public void test_set_default_message()
    {
        assertThat(router.noRouteMessages).hasSize(0);
        assertThat(router.noRouteMessages.get("test")).isNull();

        List<String> messages = new ArrayList<String>();
        messages.add("1");
        messages.add("2");

        router.setDefaultMessageForCommand("test", messages);
        assertThat(router.noRouteMessages).hasSize(1);
        assertThat(router.noRouteMessages.get("test")).containsExactly("1", "2");

        router.setDefaultMessageForCommand("test", "3");
        assertThat(router.noRouteMessages).hasSize(1);
        assertThat(router.noRouteMessages.get("test")).containsExactly("3");
    }

    @Test
    public void test_show_default_message()
    {
        router.setDefaultMessageForCommand("test", "123");

        CommandSender sender = mock(CommandSender.class);
        Command command = Bukkit.getPluginCommand("test");

        boolean s = router.onCommand(sender, command, "", new String[]{});

        assertThat(s).isTrue();
        verify(command, times(2)).getName();
        verify(sender, times(1)).sendMessage("123");
        PowerMockito.verifyNoMoreInteractions(sender, command);
    }

    @Test
    public void test_show_default_message_not_found()
    {
        CommandSender sender = mock(CommandSender.class);
        Command command = Bukkit.getPluginCommand("test");

        boolean s = router.onCommand(sender, command, "", new String[]{});

        assertThat(s).isFalse();
        verify(command, times(2)).getName();
        PowerMockito.verifyNoMoreInteractions(sender, command);
    }

    @Test
    public void test_run_command_route() throws CommandParseException
    {
        SampleCommand sample = new SampleCommand();
        router.registerCommands(sample, false);

        CommandSender sender = mock(CommandSender.class);
        Command command = Bukkit.getPluginCommand("test");

        router.onCommand(sender, command, "", new String[]{"-b", "something", "--r=6"});

        assertThat(sample.radius).isEqualTo(6);
        assertThat(sample.arguments).isEmpty();
        assertThat(sample.set.valueOf("b")).isEqualTo("something");
    }

    @Test
    public void test_run_command_route_invalid_options() throws Exception
    {
        SampleCommand sample = new SampleCommand();
        router.registerCommands(sample, false);

        CommandSender sender = mock(CommandSender.class);
        Command command = Bukkit.getPluginCommand("test");

        //call without required option 'r'
        router.onCommand(sender, command, "", new String[]{"--b=somethingelse"});

        verify(sender, never()).sendMessage(contains("\r"));
        verify(sender, times(1)).sendMessage(contains("Description"));
    }

    @Test
    public void test_run_subcommand_main_command() throws CommandParseException
    {
        SampleSubcommand sample = new SampleSubcommand();
        router.registerCommands(sample, false);

        assertThat(router.commands).hasSize(1);
        assertThat(router.commands.get("test")).hasSize(2);

        CommandSender sender = mock(CommandSender.class);
        Command command = Bukkit.getPluginCommand("test");

        router.onCommand(sender, command, "", new String[]{"-r=2"});

        assertThat(sample.commandRan).isTrue();
        assertThat(sample.subCommandRan).isFalse();
        assertThat(sample.args).hasSize(0);
    }

    @Test
    public void test_run_subcommand_sub_command() throws CommandParseException
    {
        SampleSubcommand sample = new SampleSubcommand();
        router.registerCommands(sample, false);

        assertThat(router.commands).hasSize(1);
        assertThat(router.commands.get("test")).hasSize(2);

        CommandSender sender = mock(CommandSender.class);
        Command command = Bukkit.getPluginCommand("test");

        router.onCommand(sender, command, "", new String[]{"subcommand", "-r", "-100"});

        assertThat(sample.commandRan).isFalse();
        assertThat(sample.subCommandRan).isTrue();
        assertThat(sample.args).hasSize(0);
    }

    @Test
    public void test_run_root_command_with_no_route() throws CommandParseException
    {
        SampleSubcommand sample = new SampleSubcommand();
        router.registerCommands(sample, false);

        List<CommandRoute> routes = router.commands.get("test");
        Iterator<CommandRoute> it = routes.iterator();
        while(it.hasNext()) {
            CommandRoute route = it.next();
            if(route.getStartsWith().length == 0)
                it.remove();
        }

        assertThat(router.commands).hasSize(1);
        assertThat(router.commands.get("test")).hasSize(1);

        CommandSender sender = mock(CommandSender.class);
        Command command = Bukkit.getPluginCommand("test");

        //call without subcommand
        boolean bukkitbool = router.onCommand(sender, command, "", new String[]{""});

        assertThat(sample.commandRan).isFalse();
        assertThat(sample.subCommandRan).isFalse();
        assertThat(bukkitbool).isFalse();
    }
}
