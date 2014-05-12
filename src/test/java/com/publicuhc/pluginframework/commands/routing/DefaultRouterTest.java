/*
 * DefaultRouterTest.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
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

package com.publicuhc.pluginframework.commands.routing;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.publicuhc.pluginframework.PluginModule;
import com.publicuhc.pluginframework.commands.CommandModule;
import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.annotation.TabCompletion;
import com.publicuhc.pluginframework.commands.exceptions.BaseCommandNotFoundException;
import com.publicuhc.pluginframework.commands.exceptions.CommandClassParseException;
import com.publicuhc.pluginframework.commands.exceptions.DetailsMethodNotFoundException;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.routes.Route;
import com.publicuhc.pluginframework.commands.routes.RouteBuilder;
import com.publicuhc.pluginframework.commands.routing.testcommands.*;
import com.publicuhc.pluginframework.configuration.ConfigurationModule;
import com.publicuhc.pluginframework.translate.TranslateModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, PluginCommand.class})
public class DefaultRouterTest {

    private DefaultRouter router;

    @Before
    public void setup() throws NoSuchMethodException {
        Plugin plugin = mock(Plugin.class);
        when(plugin.getDataFolder()).thenReturn(new File("target" + File.separator + "testdatafolder"));

        PluginDescriptionFile pdf = new PluginDescriptionFile("test", "1.0", "com.publicuhc.pluginframework.FrameworkJavaPlugin");
        when(plugin.getDescription()).thenReturn(pdf);

        Server server = mock(Server.class);
        when(server.getLogger()).thenReturn(Logger.getAnonymousLogger());
        when(plugin.getServer()).thenReturn(server);

        Injector injector = Guice.createInjector(
                new PluginModule(plugin),
                new CommandModule(),
                new TranslateModule(),
                new ConfigurationModule(getClass().getClassLoader()),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(TestInterface.class).to(InjectorTest.class);
                    }
                }
        );

        router = (DefaultRouter) injector.getInstance(Router.class);
    }

    @Test
    public void testRegisterCommandsInjectionObject() throws CommandClassParseException {
        TestObject object = new TestObject();
        assertThat(object.getTestInterface(), is(nullValue()));

        router.registerCommands(object, true);
        assertThat(object.getTestInterface(), is(not(nullValue())));
        assertThat(object.getTestInterface().getMessage(), is(equalTo("SUCCESS")));
    }

    @Test
    public void testRegisterCommandsInjectionClass() throws CommandClassParseException {
        Object object = router.registerCommands(TestObject.class);
        assertTrue(object instanceof TestObject);
        TestObject testObject = (TestObject) object;
        assertThat(testObject.getTestInterface(), is(not(nullValue())));
        assertThat(testObject.getTestInterface().getMessage(), is(equalTo("SUCCESS")));
    }

    /*###############################
     *#  Tests for registerCommands #
     *#############################*/

    @Test
    public void testRegisterCommandsValid() throws CommandClassParseException {
        //no commands at all, should pass
        router.registerCommands(TestEmptyCommands.class);
        router.registerCommands(new TestEmptyCommands(), true);

        mockStatic(Bukkit.class);
        when(Bukkit.getPluginCommand("test")).thenReturn(mock(PluginCommand.class));

        router.registerCommands(TestValidCommands.class);
        router.registerCommands(new TestValidCommands(), true);
    }

    @Test(expected = BaseCommandNotFoundException.class)
    public void testRegisterCommandsUnknownCommand() throws CommandClassParseException {
        mockStatic(Bukkit.class);
        when(Bukkit.getPluginCommand("unknowncommand")).thenReturn(null);

        router.registerCommands(TestMissingBaseCommands.class);
    }

    @Test(expected = DetailsMethodNotFoundException.class)
    public void testDetailsMissingCommand() throws CommandClassParseException {
        router.registerCommands(TestDetailsMissingCommands.class);
    }

    @Test(expected = CommandClassParseException.class)
    public void testExceptionRouteMethod() throws CommandClassParseException {
        router.registerCommands(TestExceptionRouteInfoCommands.class);
    }

    /*###################
     *#  test onCommand #
     *#################*/

    @Test
    public void testOnCommandSuccess() throws CommandClassParseException {
        mockStatic(Bukkit.class);
        PluginCommand command = mock(PluginCommand.class);
        when(command.getName()).thenReturn("banIP");
        when(Bukkit.getPluginCommand("banIP")).thenReturn(command);
        router.registerCommands(TestFullCommands.class);

        CommandSender sender = mock(ConsoleCommandSender.class);
        when(sender.hasPermission("test.permission")).thenReturn(true);
        router.onCommand(sender, command, "wtfisalabel", new String[]{"192.168.0.1", "some", "random", "message", "here"});

        verify(sender).sendMessage("banned with message some");
        verifyStatic();
        Bukkit.banIP("192.168.0.1");
    }

    @Test
    public void testOnCommandMissing() {
        mockStatic(Bukkit.class);
        CommandSender sender = mock(ConsoleCommandSender.class);
        PluginCommand command = mock(PluginCommand.class);
        when(command.getName()).thenReturn("banIP");
        when(Bukkit.getPluginCommand("banIP")).thenReturn(command);
        router.setDefaultMessageForCommand("banIP", "banip route not found");

        router.onCommand(sender, command, "wtfisalabel", new String[]{"192.168.0.1", "some", "random", "message", "here"});
        verify(sender).sendMessage("banip route not found");
    }

    @Test
    public void testExceptionRunningCommand() throws CommandClassParseException {
        mockStatic(Bukkit.class);
        CommandSender sender = mock(ConsoleCommandSender.class);
        PluginCommand command = mock(PluginCommand.class);
        when(command.getName()).thenReturn("test");
        when(Bukkit.getPluginCommand("test")).thenReturn(command);

        router.registerCommands(TestExceptionCommandMethod.class);
        router.onCommand(sender, command, "wtfisalabel", new String[]{});

        verify(sender).sendMessage(ChatColor.RED + "Error running command, check console for more information");
    }

    @Test
    public void testOnTabCompleteSuccess() throws CommandClassParseException {
        mockStatic(Bukkit.class);
        CommandSender sender = mock(ConsoleCommandSender.class);
        when(sender.hasPermission("test.permission")).thenReturn(true);
        PluginCommand command = mock(PluginCommand.class);
        when(command.getName()).thenReturn("banIP");
        when(Bukkit.getPluginCommand("banIP")).thenReturn(command);
        router.registerCommands(TestFullCommands.class);

        List<String> complete = router.onTabComplete(sender, command, "wtfisalabel", new String[]{"192.168.0.1"});

        assertThat(complete, is(equalTo(Arrays.asList("1", "2", "3"))));
    }

    @Test
    public void testOnTabCompleteMissing() {
        mockStatic(Bukkit.class);
        CommandSender sender = mock(ConsoleCommandSender.class);
        PluginCommand command = mock(PluginCommand.class);
        when(command.getName()).thenReturn("test");
        when(Bukkit.getPluginCommand("test")).thenReturn(command);
        assertThat(router.onTabComplete(sender, command, "wtfisalabel", new String[]{"192.168.0.1"}), is(equalTo(Arrays.asList(new String[]{}))));
    }

    @Test
    public void testExceptionRunningTabComplete() throws CommandClassParseException {
        mockStatic(Bukkit.class);
        CommandSender sender = mock(ConsoleCommandSender.class);
        PluginCommand command = mock(PluginCommand.class);
        when(command.getName()).thenReturn("test");
        when(Bukkit.getPluginCommand("test")).thenReturn(command);

        router.registerCommands(TestExceptionCommandMethod.class);
        router.onTabComplete(sender, command, "wtfisalabel", new String[]{});

        verify(sender).sendMessage(ChatColor.RED + "Error with tab completion, check the console for more information");
    }

    @SuppressWarnings("unused")
    public static class TestCommandClass {

        /**
         * Test for checking invalid return type
         *
         * @param request n/a
         */
        @TabCompletion
        public void onTabCompleteNoReturn(CommandRequest request) {

        }

        /**
         * Test for checking invalid parameters
         *
         * @return n/a
         */
        @TabCompletion
        public List<String> onTabCompleteNoParam() {
            return null;
        }

        /**
         * Test for checking a valid method
         *
         * @param request n/a
         * @return n/a
         */
        @TabCompletion
        public List<String> onValidTabComplete(CommandRequest request) {
            return null;
        }

        /**
         * Test for checking an invalid return type
         *
         * @param request n/a
         * @return n/a
         */
        @TabCompletion
        public String onInvalidReturnTypeTabComplete(CommandRequest request) {
            return null;
        }

        /**
         * Test for checking an invalid return type
         *
         * @param request n/a
         * @return n/a
         */
        @TabCompletion
        public List<Integer> onInvalidGenericReturnTypeTabComplete(CommandRequest request) {
            return null;
        }

        /**
         * Test for checking missing annotation
         *
         * @param request n/a
         * @return n/a
         */
        public List<String> onMissingAnnotationTabComplete(CommandRequest request) {
            return null;
        }

        /**
         * Test for a valid command method
         *
         * @param request n/a
         */
        @CommandMethod
        public void onValidCommandMethod(CommandRequest request) {}

        /**
         * Test for invalid parameters
         *
         * @param string n/a
         */
        @CommandMethod
        public void onInvalidParameters(String string) {}

        /**
         * Test for missing annotation
         *
         * @param request n/a
         */
        public void onMissingAnnotationCommandMethod(CommandRequest request) {}

        /**
         * Test for a valid route info
         */
        @RouteInfo
        public void onValidRouteInfo(RouteBuilder builder) {}

        /**
         * Test for missing annotation
         *
         * @return n/a
         */
        public Route onMissingAnnotationRouteInfo(RouteBuilder builder) {
            return null;
        }
    }

    private static class TestObject {

        private TestInterface testInterface = null;

        @Inject
        public void setInterface(TestInterface testInterface) {
            this.testInterface = testInterface;
        }

        public TestInterface getTestInterface() {
            return testInterface;
        }
    }

    private static interface TestInterface {
        String getMessage();
    }

    private static class InjectorTest implements TestInterface {
        public String getMessage() {
            return "SUCCESS";
        }
    }
}
