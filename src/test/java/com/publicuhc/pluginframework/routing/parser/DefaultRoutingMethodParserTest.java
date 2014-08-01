/*
 * DefaultRoutingMethodParserTest.java
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

package com.publicuhc.pluginframework.routing.parser;

import com.publicuhc.pluginframework.routing.CommandMethod;
import com.publicuhc.pluginframework.routing.CommandRequest;
import com.publicuhc.pluginframework.routing.CommandRoute;
import com.publicuhc.pluginframework.routing.exception.AnnotationMissingException;
import com.publicuhc.pluginframework.routing.exception.CommandParseException;
import com.publicuhc.pluginframework.routing.proxy.MethodProxy;
import junit.framework.AssertionFailedError;
import org.bukkit.block.CommandBlock;
import org.bukkit.craftbukkit.libs.joptsimple.OptionException;
import org.bukkit.craftbukkit.libs.joptsimple.OptionParser;
import org.bukkit.craftbukkit.libs.joptsimple.OptionSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
public class DefaultRoutingMethodParserTest
{
    private DefaultRoutingMethodParser parser;

    @Before
    public void onStartup()
    {
        parser = new DefaultRoutingMethodParser();
    }

    //sample methods to use in tests
    @CommandMethod(command = "test", options = true, permission = "TEST.PERMISSION")
    public String commandWithAnnotation(CommandRequest request)
    {
        return "TEST_STRING";
    }

    public String[] commandWithAnnotation(OptionParser optionParser)
    {
        optionParser.accepts("a").withRequiredArg().ofType(Integer.class);
        optionParser.accepts("b").withOptionalArg().ofType(String.class);

        return new String[]{"a"};
    }

    public void commandWithoutAnnotation(CommandRequest request)
    {}

    @CommandMethod(command = "test", options = true)
    public String commandWithAnnotationOptions(CommandRequest request)
    {
        return "TEST_STRING";
    }

    public String[] commandWithAnnotationOptions(OptionParser optionParser)
    {
        optionParser.accepts("a").withRequiredArg();
        optionParser.accepts("b").withOptionalArg();

        return new String[]{"a"};
    }

    @CommandMethod(command = "test", options = true)
    public void commandWithAnnotationButNoOptions(CommandRequest request)
    {}

    @CommandMethod(command = "test")
    public void commandWithExtraArgs(CommandRequest request, String[] extra)
    {}

    @CommandMethod(command = "test")
    public void commandWithNotEnoughArgs()
    {}

    @CommandMethod(command = "test")
    public void commandWithWrongArg1(CommandBlock command)
    {}

    @CommandMethod(command = "test", options = true)
    public void commandWithInvalidOptionsReturn(CommandRequest request)
    {}

    public void commandWithInvalidOptionsReturn(OptionParser optionParser)
    {}

    @CommandMethod(command = "test", options = true)
    public void commandWithInvalidOptionsParam(CommandRequest request)
    {}

    public String[] commandWithInvalidOptionsParam(OptionSet set)
    {
        return new String[]{};
    }

    //helpful methods
    private void assertOptionsValid(CommandOptionsParser optionParser)
    {
        OptionSet set = optionParser.parse("-a", "299");
        assertThat(set.hasArgument("a")).isTrue();
        assertThat(set.hasArgument("b")).isFalse();
        assertThat(set.valueOf("a")).isEqualTo(299);

        set = optionParser.parse("bleh", "--a=2", "-b=test", "random", "words");
        assertThat(set.hasArgument("a")).isTrue();
        assertThat(set.hasArgument("b")).isTrue();
        assertThat(set.valueOf("a")).isEqualTo(2);
        assertThat(set.valueOf("b")).isEqualTo("test");
        assertThat(set.nonOptionArguments()).containsExactly("bleh", "random", "words");
    }

    private void assertOptionsValidStrings(CommandOptionsParser optionParser)
    {
        OptionSet set = optionParser.parse("-a", "299");
        assertThat(set.hasArgument("a")).isTrue();
        assertThat(set.hasArgument("b")).isFalse();
        assertThat(set.valueOf("a")).isEqualTo("299");

        set = optionParser.parse("bleh", "--a=2", "-b=test", "random", "words");
        assertThat(set.hasArgument("a")).isTrue();
        assertThat(set.hasArgument("b")).isTrue();
        assertThat(set.valueOf("a")).isEqualTo("2");
        assertThat(set.valueOf("b")).isEqualTo("test");
        assertThat(set.nonOptionArguments()).containsExactly("bleh", "random", "words");

        try {
            optionParser.parse("-b=test");
            throw new AssertionFailedError("Expected OptionException for missing argument a");
        } catch(OptionException ignored) {
        }
    }

    //actual tests
    @Test
    public void test_has_command_method_annotation() throws NoSuchMethodException
    {
        Method valid = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotation", CommandRequest.class);
        assertThat(parser.hasCommandMethodAnnotation(valid)).isTrue();

        Method invalid = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithoutAnnotation", CommandRequest.class);
        assertThat(parser.hasCommandMethodAnnotation(invalid)).isFalse();
    }

    @Test
    public void test_get_options_method() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotation", CommandRequest.class);
        CommandOptionsParser optionParser = parser.getOptionsForMethod(method, this);
        assertOptionsValid(optionParser);
    }

    @Test
    public void test_parse_permissions() throws NoSuchMethodException, CommandParseException {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotation", CommandRequest.class);

        CommandRoute route = parser.parseCommandMethodAnnotation(method, this);
        assertThat(route.getPermission().equals("TEST.PERMISSION"));

        method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotationOptions", CommandRequest.class);

        route = parser.parseCommandMethodAnnotation(method, this);
        assertThat(route.getPermission()).isNull();
    }

    @Test
    public void test_parse_method_options_method() throws Throwable
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotation", CommandRequest.class);
        CommandRoute route = parser.parseCommandMethodAnnotation(method, this);

        assertThat(route.getCommandName()).isEqualTo("test");
        assertOptionsValid(route.getOptionDetails());

        MethodProxy proxy = route.getProxy();
        assertThat(proxy.getInstance()).isSameAs(this);
        assertThat(proxy.invoke(mock(CommandRequest.class))).isEqualTo("TEST_STRING");
    }

    @Test
    public void test_parse_method_options_method_missing() throws NoSuchMethodException
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotationButNoOptions", CommandRequest.class);

        try {
            parser.parseCommandMethodAnnotation(method, this);
            throw new AssertionFailedError("Expected CommandParseException");
        } catch(CommandParseException ex) {
            Throwable cause = ex.getCause();
            assertThat(cause).isInstanceOf(NoSuchMethodException.class);
        }
    }

    @Test
    public void test_parse_method_options_method_invalid_return() throws NoSuchMethodException
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithInvalidOptionsReturn", CommandRequest.class);

        try {
            parser.parseCommandMethodAnnotation(method, this);
            throw new AssertionFailedError("Expected CommandParseException");
        } catch(CommandParseException ex) {
            Throwable cause = ex.getCause();
            assertThat(cause).isInstanceOf(NoSuchMethodException.class);
        }
    }

    @Test
    public void test_parse_method_options_method_invalid_parameters() throws NoSuchMethodException
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithInvalidOptionsParam", CommandRequest.class);

        try {
            parser.parseCommandMethodAnnotation(method, this);
            throw new AssertionFailedError("Expected CommandParseException");
        } catch(CommandParseException ex) {
            Throwable cause = ex.getCause();
            assertThat(cause).isInstanceOf(NoSuchMethodException.class);
        }
    }

    @Test
    public void test_parse_method_no_annotation() throws NoSuchMethodException
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithoutAnnotation", CommandRequest.class);

        try {
            parser.parseCommandMethodAnnotation(method, this);
            throw new AssertionFailedError("Expected CommandParseException");
        } catch(CommandParseException ex) {
            assertThat(ex).isInstanceOf(AnnotationMissingException.class);
        }
    }

    @Test
    public void test_parse_method_annotation_with_options() throws Throwable
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotationOptions", CommandRequest.class);

        CommandRoute route = parser.parseCommandMethodAnnotation(method, this);

        assertThat(route.getCommandName()).isEqualTo("test");
        assertOptionsValidStrings(route.getOptionDetails());
        assertThat(route.getProxy().invoke(mock(CommandRequest.class))).isEqualTo("TEST_STRING");
    }

    @Test
    public void test_command_method_parameters_correct() throws NoSuchMethodException
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithExtraArgs", CommandRequest.class, String[].class);
        assertThat(parser.areCommandMethodParametersCorrect(method)).isFalse();

        method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithNotEnoughArgs");
        assertThat(parser.areCommandMethodParametersCorrect(method)).isFalse();

        method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithWrongArg1", CommandBlock.class);
        assertThat(parser.areCommandMethodParametersCorrect(method)).isFalse();

        method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotation", CommandRequest.class);
        assertThat(parser.areCommandMethodParametersCorrect(method)).isTrue();
    }

    @Test
    public void test_parse_command_method_invalid_args() throws NoSuchMethodException
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithExtraArgs", CommandRequest.class, String[].class);

        try {
            parser.parseCommandMethodAnnotation(method, this);
            throw new AssertionFailedError("Expected CommandParseException");
        } catch(CommandParseException ex) {
            assertThat(ex.getMessage()).contains("Invalid command method parameters");
        }
    }
}
