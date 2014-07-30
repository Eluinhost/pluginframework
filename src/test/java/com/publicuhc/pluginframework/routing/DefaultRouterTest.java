package com.publicuhc.pluginframework.routing;

import com.google.inject.Injector;
import com.publicuhc.pluginframework.routing.exception.CommandParseException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.libs.joptsimple.OptionParser;
import org.bukkit.craftbukkit.libs.joptsimple.OptionSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

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

    @Before
    public void onStartup()
    {
        injector = mock(Injector.class);
        when(injector.getInstance(ValidCommandClass.class)).thenReturn(new ValidCommandClass());
        router = new DefaultRouter(new DefaultRoutingMethodParser(), injector);

        PluginCommand command = mock(PluginCommand.class);
        when(command.getName()).thenReturn("testcommand");

        mockStatic(Bukkit.class);
        when(Bukkit.getPluginCommand("testcommand")).thenReturn(command);
    }

    @Test
    public void test_register_command_by_class() throws Throwable
    {
        router.registerCommands(ValidCommandClass.class);

        assertThat(router.commands).hasSize(1);
        CommandRoute route = router.commands.get("testcommand");
        assertThat(route).isNotNull();
        assertThat(route.getCommandName()).isEqualTo("testcommand");
        assertThat(route.getProxy().getInstance()).isInstanceOf(ValidCommandClass.class);

        CommandSender sender = mock(CommandSender.class);
        route.getProxy().invoke(mock(Command.class), sender, mock(OptionSet.class));
        verify(sender, times(1)).sendMessage("success");
        verifyNoMoreInteractions(sender);

        verify(injector, times(1)).getInstance(ValidCommandClass.class);
        verify(injector, never()).injectMembers(any(ValidCommandClass.class));
        PowerMockito.verifyNoMoreInteractions(injector);
    }

    @Test
    public void test_register_command_by_instance() throws Throwable
    {
        router.registerCommands(new ValidCommandClass(), true);

        assertThat(router.commands).hasSize(1);
        CommandRoute route = router.commands.get("testcommand");
        assertThat(route).isNotNull();
        assertThat(route.getCommandName()).isEqualTo("testcommand");
        assertThat(route.getProxy().getInstance()).isInstanceOf(ValidCommandClass.class);

        CommandSender sender = mock(CommandSender.class);
        route.getProxy().invoke(mock(Command.class), sender, mock(OptionSet.class));
        verify(sender, times(1)).sendMessage("success");
        verifyNoMoreInteractions(sender);

        verify(injector, never()).getInstance(ValidCommandClass.class);
        verify(injector, times(1)).injectMembers(any(ValidCommandClass.class));
        PowerMockito.verifyNoMoreInteractions(injector);
    }

    @Test(expected = CommandParseException.class)
    public void test_invalid_command_register() throws Throwable
    {
        router.registerCommands(new InvalidCommandClass(), true);
    }

    @Test
    public void test_set_default_message()
    {
        assertThat(router.noRouteMessages).hasSize(0);
        assertThat(router.noRouteMessages.get("testcommand")).isNull();

        List<String> messages = new ArrayList<String>();
        messages.add("1");
        messages.add("2");

        router.setDefaultMessageForCommand("testcommand", messages);
        assertThat(router.noRouteMessages).hasSize(1);
        assertThat(router.noRouteMessages.get("testcommand")).containsExactly("1", "2");

        router.setDefaultMessageForCommand("testcommand", "3");
        assertThat(router.noRouteMessages).hasSize(1);
        assertThat(router.noRouteMessages.get("testcommand")).containsExactly("3");
    }

    @Test
    public void test_show_default_message()
    {
        router.setDefaultMessageForCommand("testcommand", "123");

        CommandSender sender = mock(CommandSender.class);
        Command command = Bukkit.getPluginCommand("testcommand");

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
        Command command = Bukkit.getPluginCommand("testcommand");

        boolean s = router.onCommand(sender, command, "", new String[]{});

        assertThat(s).isFalse();
        verify(command, times(2)).getName();
        PowerMockito.verifyNoMoreInteractions(sender, command);
    }

    @Test
    public void test_run_command_route() throws CommandParseException
    {
        SampleCommandClass sample = new SampleCommandClass();
        router.registerCommands(sample, false);

        CommandSender sender = mock(CommandSender.class);
        Command command = Bukkit.getPluginCommand("testcommand");

        router.onCommand(sender, command, "", new String[]{"-a", "something", "--b=somethingelse"});

        assertThat(sample.lastOptionSet).isNotNull();
        assertThat(sample.lastOptionSet.hasArgument("a")).isTrue();
        assertThat(sample.lastOptionSet.hasArgument("b")).isTrue();
        assertThat(sample.lastOptionSet.hasArgument("c")).isFalse();
        assertThat(sample.lastOptionSet.valueOf("a")).isEqualTo("something");
        assertThat(sample.lastOptionSet.valueOf("b")).isEqualTo("somethingelse");
    }

    @Test
    public void test_run_command_route_invalid_options() throws CommandParseException
    {
        SampleCommandClass sample = new SampleCommandClass();
        router.registerCommands(sample, false);

        CommandSender sender = mock(CommandSender.class);
        Command command = Bukkit.getPluginCommand("testcommand");

        //call without required option 'a'
        router.onCommand(sender, command, "", new String[]{"--b=somethingelse"});

        System.out.println(sample.lastOptionSet);
        verify(sender, times(1)).sendMessage("2jj");
    }

    public class SampleCommandClass
    {
        public OptionSet lastOptionSet;

        @CommandMethod(command = "testcommand", options = true)
        public void testCommand(Command command, CommandSender sender, OptionSet set)
        {
            lastOptionSet = set;
        }

        public void testCommand(OptionParser parser)
        {
            parser.accepts("a").withRequiredArg();
            parser.accepts("b").withOptionalArg();
        }
    }

    public class ValidCommandClass
    {
        @CommandMethod(command = "testcommand")
        public void testCommand(Command command, CommandSender sender, OptionSet set)
        {
            sender.sendMessage("success");
        }
    }

    public class InvalidCommandClass
    {
        //invalid because missing options method
        @CommandMethod(command = "testcommand")
        public void testCommand(Command command, CommandSender sender, OptionSet set)
        {}
    }
}