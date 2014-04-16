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
import com.google.inject.name.Names;
import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.annotation.TabCompletion;
import com.publicuhc.pluginframework.commands.exceptions.*;
import com.publicuhc.pluginframework.commands.proxies.CommandProxy;
import com.publicuhc.pluginframework.commands.proxies.TabCompleteProxy;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.requests.CommandRequestBuilder;
import com.publicuhc.pluginframework.commands.requests.DefaultCommandRequestBuilder;
import com.publicuhc.pluginframework.commands.routing.testcommands.*;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.configuration.DefaultConfigurator;
import com.publicuhc.pluginframework.translate.DefaultTranslate;
import com.publicuhc.pluginframework.translate.Translate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, PluginCommand.class})
public class DefaultRouterTest {

    private DefaultRouter router;
    private Injector injector;

    @Before
    public void setup() throws NoSuchMethodException {
       injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Router.class).to(DefaultRouter.class);
                bind(CommandRequestBuilder.class).to(DefaultCommandRequestBuilder.class);
                bind(TestInterface.class).to(InjectorTest.class);
                bind(Translate.class).to(DefaultTranslate.class);
                bind(Configurator.class).to(DefaultConfigurator.class);
                bind(Plugin.class).toInstance(mock(Plugin.class));
                bind(File.class).annotatedWith(Names.named("dataFolder")).toInstance(new File("target"+File.separator+"testdatafolder"));
            }
        });
        router = (DefaultRouter) injector.getInstance(Router.class);
    }

    @After
    public void teardown(){
        router = null;
    }

    /*####################################
     *  Tests for checkTabCompleteReturn #
     *##################################*/

    @Test
    public void testValidTabCompleteReturn() throws NoSuchMethodException, InvalidReturnTypeException {
        Method validTabComplete = TestCommandClass.class.getMethod("onValidTabComplete", CommandRequest.class);
        router.checkTabCompleteReturn(validTabComplete);
    }

    @Test(expected = InvalidReturnTypeException.class)
    public void testVoidTabCompleteReturn() throws NoSuchMethodException, InvalidReturnTypeException {
        Method invalidReturn = TestCommandClass.class.getMethod("onTabCompleteNoReturn", CommandRequest.class);
        router.checkTabCompleteReturn(invalidReturn);
    }

    @Test(expected = InvalidReturnTypeException.class)
    public void testInvalidTypeTabCompleteReturn() throws NoSuchMethodException, InvalidReturnTypeException {
        Method invalidReturn = TestCommandClass.class.getMethod("onInvalidReturnTypeTabComplete", CommandRequest.class);
        router.checkTabCompleteReturn(invalidReturn);
    }

    @Test(expected = InvalidReturnTypeException.class)
    public void testInvalidGenericReturnTabCompleteReturn() throws NoSuchMethodException, InvalidReturnTypeException {
        Method invalidReturn = TestCommandClass.class.getMethod("onInvalidGenericReturnTypeTabComplete", CommandRequest.class);
        router.checkTabCompleteReturn(invalidReturn);
    }

    /*###########################
     *  Tests for isTabComplete #
     *#########################*/

    @Test
    public void testValidTabCompleteAnnotation() throws NoSuchMethodException {
        Method validAnnotation = TestCommandClass.class.getMethod("onValidTabComplete", CommandRequest.class);
        assertTrue(router.isTabComplete(validAnnotation));
    }

    @Test
    public void testMissingTabCompleteAnnotation() throws NoSuchMethodException {
        Method missingAnnotation = TestCommandClass.class.getMethod("onMissingAnnotationTabComplete", CommandRequest.class);
        assertFalse(router.isTabComplete(missingAnnotation));
    }

    /*#############################
     *  Tests for isCommandMethod #
     *###########################*/

    @Test
    public void testValidCommandMethodAnnotation() throws NoSuchMethodException {
        Method validAnnotation = TestCommandClass.class.getMethod("onValidCommandMethod", CommandRequest.class);
        assertTrue(router.isCommandMethod(validAnnotation));
    }

    @Test
    public void testMissingCommandMethodAnnotation() throws NoSuchMethodException {
        Method missingAnnotation = TestCommandClass.class.getMethod("onMissingAnnotationCommandMethod", CommandRequest.class);
        assertFalse(router.isCommandMethod(missingAnnotation));
    }

    /*############################
     *  Tests for checkRouteInfo #
     *##########################*/

    @Test
    public void testValidRouteInfo() throws NoSuchMethodException, CommandClassParseException {
        Method validRouteInfo = TestCommandClass.class.getMethod("onValidRouteInfo");
        router.checkRouteInfo(validRouteInfo);
    }

    @Test(expected = AnnotationMissingException.class)
    public void testMissingAnnotationRouteInfo() throws NoSuchMethodException, CommandClassParseException {
        Method routeInfo = TestCommandClass.class.getMethod("onMissingAnnotationRouteInfo");
        router.checkRouteInfo(routeInfo);
    }

    @Test(expected = InvalidReturnTypeException.class)
    public void testMissingReturnRouteInfo() throws NoSuchMethodException, CommandClassParseException {
        Method routeInfo = TestCommandClass.class.getMethod("onMissingReturnRouteInfo");
        router.checkRouteInfo(routeInfo);
    }

    @Test(expected = InvalidReturnTypeException.class)
    public void testInvalidReturnRouteInfo() throws NoSuchMethodException, CommandClassParseException {
        Method routeInfo = TestCommandClass.class.getMethod("onInvalidReturnRouteInfo");
        router.checkRouteInfo(routeInfo);
    }

    /*#############################
     *  Tests for checkParameters #
     *###########################*/

    @Test
    public void testValidParameters() throws NoSuchMethodException, CommandClassParseException {
        Method valid = TestCommandClass.class.getMethod("onValidCommandMethod", CommandRequest.class);
        router.checkParameters(valid);
    }

    @Test(expected = InvalidMethodParametersException.class)
    public void testInvalidParameters() throws NoSuchMethodException, CommandClassParseException {
        Method invalid = TestCommandClass.class.getMethod("onInvalidParameters", String.class);
        router.checkParameters(invalid);
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

    /*#########################
     *#  test getCommandProxy #
     *#######################*/

    @Test
    public void testGetCommandProxy() throws CommandClassParseException {
        mockStatic(Bukkit.class);
        PluginCommand command = mock(PluginCommand.class);
        when(command.getName()).thenReturn("test");
        when(Bukkit.getPluginCommand("test")).thenReturn(command);
        router.registerCommands(TestValidCommands.class);

        Command valid = mock(Command.class);
        when(valid.getName()).thenReturn("test");

        Map<CommandProxy, MatchResult> proxies = router.getCommandProxy(valid, "arg");
        assertThat(proxies.size(), is(1));

        Command invalid = mock(Command.class);
        when(invalid.getName()).thenReturn("invalidtest");

        proxies = router.getCommandProxy(invalid, "arg");
        assertThat(proxies.size(), is(0));
    }

    /*#############################
     *#  test getTabCompleteProxy #
     *###########################*/

    @Test
    public void testGetTabCompleteProxy() throws CommandClassParseException {
        mockStatic(Bukkit.class);
        PluginCommand command = mock(PluginCommand.class);
        when(command.getName()).thenReturn("test");
        when(Bukkit.getPluginCommand("test")).thenReturn(command);
        router.registerCommands(TestValidCommands.class);

        Command valid = mock(Command.class);
        when(valid.getName()).thenReturn("test");

        Map<TabCompleteProxy, MatchResult> proxies = router.getTabCompleteProxy(valid, "arg");
        assertThat(proxies.size(), is(1));

        Command invalid = mock(Command.class);
        when(invalid.getName()).thenReturn("invalidtest");

        proxies = router.getTabCompleteProxy(invalid, "arg");
        assertThat(proxies.size(), is(0));
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
        router.onCommand(sender, command, "wtfisalabel", new String[]{"192.168.0.1", "some", "random", "message", "here"});

        verify(sender).sendMessage("banned with message some random message here");
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
        assertFalse(router.onCommand(sender, command, "wtfisalabel", new String[]{"192.168.0.1", "some", "random", "message", "here"}));
        //TODO check for default command message if no route found
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

        verify(sender).sendMessage(ChatColor.RED+"Error running command, check console for more information");
    }

    @Test
    public void testOnTabCompleteSuccess() throws CommandClassParseException {
        mockStatic(Bukkit.class);
        CommandSender sender = mock(ConsoleCommandSender.class);
        PluginCommand command = mock(PluginCommand.class);
        when(command.getName()).thenReturn("banIP");
        when(Bukkit.getPluginCommand("banIP")).thenReturn(command);
        router.registerCommands(TestFullCommands.class);

        List<String> complete = router.onTabComplete(sender, command, "wtfisalabel", new String[]{"192.168.0.1"});

        assertThat(complete, is(equalTo(Arrays.asList("1","2","3"))));
    }

    @Test
    public void testOnTabCompleteMissing() {
        mockStatic(Bukkit.class);
        CommandSender sender = mock(ConsoleCommandSender.class);
        PluginCommand command = mock(PluginCommand.class);
        when(command.getName()).thenReturn("test");
        when(Bukkit.getPluginCommand("test")).thenReturn(command);
        assertThat(router.onTabComplete(sender, command, "wtfisalabel", new String[]{"192.168.0.1"}), is(equalTo(Arrays.asList(new String[]{}))));
        //TODO check for default command message if no route found
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
    private static class TestCommandClass {

        /**
         * Test for checking invalid return type
         * @param request n/a
         */
        @TabCompletion
        public void onTabCompleteNoReturn(CommandRequest request) {

        }

        /**
         * Test for checking invalid parameters
         * @return n/a
         */
        @TabCompletion
        public List<String> onTabCompleteNoParam() {
            return null;
        }

        /**
         * Test for checking a valid method
         * @param request n/a
         * @return n/a
         */
        @TabCompletion
        public List<String> onValidTabComplete(CommandRequest request) {
            return null;
        }

        /**
         * Test for checking an invalid return type
         * @param request n/a
         * @return n/a
         */
        @TabCompletion
        public String onInvalidReturnTypeTabComplete(CommandRequest request) {
            return null;
        }

        /**
         * Test for checking an invalid return type
         * @param request n/a
         * @return n/a
         */
        @TabCompletion
        public List<Integer> onInvalidGenericReturnTypeTabComplete(CommandRequest request) {
            return null;
        }

        /**
         * Test for checking missing annotation
         * @param request n/a
         * @return n/a
         */
        public List<String> onMissingAnnotationTabComplete(CommandRequest request) {
            return null;
        }

        /**
         * Test for a valid command method
         * @param request n/a
         */
        @CommandMethod
        public void onValidCommandMethod(CommandRequest request) {
        }

        /**
         * Test for invalid parameters
         * @param string n/a
         */
        @CommandMethod
        public void onInvalidParameters(String string) {
        }

        /**
         * Test for missing annotation
         * @param request n/a
         */
        public void onMissingAnnotationCommandMethod(CommandRequest request) {
        }

        /**
         * Test for a valid route info
         * @return n/a
         */
        @RouteInfo
        public MethodRoute onValidRouteInfo() {
            return null;
        }

        /**
         * Test for missing annotation
         * @return n/a
         */
        public MethodRoute onMissingAnnotationRouteInfo() {
            return null;
        }

        /**
         * Test for missing return type
         */
        @RouteInfo
        public void onMissingReturnRouteInfo() {
        }

        /**
         * Test for invalid return type
         * @return n/a
         */
        @RouteInfo
        public String onInvalidReturnRouteInfo() {
            return null;
        }
    }

    private static class TestObject {

        private TestInterface testInterface = null;

        @Inject
        public void setInterface(TestInterface testInterface){
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
