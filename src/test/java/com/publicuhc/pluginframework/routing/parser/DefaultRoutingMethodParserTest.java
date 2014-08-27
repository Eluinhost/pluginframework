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
import com.publicuhc.pluginframework.routing.converters.LocationValueConverter;
import com.publicuhc.pluginframework.routing.converters.OnlinePlayerValueConverter;
import com.publicuhc.pluginframework.routing.exception.AnnotationMissingException;
import com.publicuhc.pluginframework.routing.exception.CommandParseException;
import com.publicuhc.pluginframework.routing.proxy.MethodProxy;
import joptsimple.OptionDeclarer;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import junit.framework.AssertionFailedError;
import org.bukkit.Location;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

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

    public void commandWithAnnotation(OptionDeclarer optionParser)
    {
        optionParser.accepts("a").withRequiredArg().ofType(Integer.class).required();
        optionParser.accepts("b").withOptionalArg().ofType(String.class);
    }

    @CommandMethod(command = "test", allowedSenders = Player.class)
    public String commandWithSenderRestriction(CommandRequest request)
    {
        return "TEST_STRING";
    }

    @CommandMethod(command = "test subcommand system")
    public String commandWithStartsWithRestriction(CommandRequest request)
    {
        return "TEST_STRING";
    }

    public void commandWithoutAnnotation(CommandRequest request)
    {}

    @CommandMethod(command = "test", options = true)
    public String commandWithAnnotationOptions(CommandRequest request)
    {
        return "TEST_STRING";
    }

    public void commandWithAnnotationOptions(OptionDeclarer optionParser)
    {
        optionParser.accepts("a").withRequiredArg().required();
        optionParser.accepts("b").withOptionalArg();
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

    public String[] commandWithInvalidOptionsReturn(OptionDeclarer optionParser)
    {return null;}

    @CommandMethod(command = "test", options = true)
    public void commandWithInvalidOptionsParam(CommandRequest request)
    {}

    public void commandWithInvalidOptionsParam(OptionSet set)
    {}

    @CommandMethod(command = "test", helpOption = "t", options = true)
    public void commandWithNonStandardHelp(CommandRequest request)
    {}

    public void commandWithNonStandardHelp(OptionDeclarer optionParser)
    {}

    //helpful methods
    private void assertOptionsValid(OptionParser optionParser)
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

    private void assertOptionsValidStrings(OptionParser optionParser)
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
        OptionParser optionParser = parser.getOptionsForMethod(method, this);
        assertOptionsValid(optionParser);
    }

    @Test
    public void test_parse_permissions() throws NoSuchMethodException, CommandParseException
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotation", CommandRequest.class);

        CommandRoute route = parser.parseCommandMethodAnnotation(method, this);
        assertThat(route.getPermission().equals("TEST.PERMISSION"));
        assertThat(route.getOptionDetails().recognizedOptions()).containsKey("?");
        assertThat(route.getOptionDetails().recognizedOptions()).doesNotContainKey("t");

        method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotationOptions", CommandRequest.class);

        route = parser.parseCommandMethodAnnotation(method, this);
        assertThat(route.getPermission()).isNull();
        assertThat(route.getOptionDetails().recognizedOptions()).containsKey("?");
        assertThat(route.getOptionDetails().recognizedOptions()).doesNotContainKey("t");
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
        assertThat(route.getOptionDetails().recognizedOptions()).containsKey("?");
        assertThat(route.getOptionDetails().recognizedOptions()).doesNotContainKey("t");
        assertThat(route.getStartsWith()).isEmpty();
        //noinspection unchecked
        assertThat(route.getAllowedSenders()).containsExactly(CommandSender.class);
    }

    @Test
    public void test_starts_with() throws Throwable
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithStartsWithRestriction", CommandRequest.class);
        CommandRoute route = parser.parseCommandMethodAnnotation(method, this);

        assertThat(route.getStartsWith()).containsExactly("subcommand", "system");
    }

    @Test
    public void test_parse_method_chosen_senders() throws Throwable
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithSenderRestriction", CommandRequest.class);
        CommandRoute route = parser.parseCommandMethodAnnotation(method, this);

        assertThat(route.getCommandName()).isEqualTo("test");

        MethodProxy proxy = route.getProxy();
        assertThat(proxy.getInstance()).isSameAs(this);
        assertThat(proxy.invoke(mock(CommandRequest.class))).isEqualTo("TEST_STRING");
        assertThat(route.getOptionDetails().recognizedOptions()).containsKey("?");
        assertThat(route.getOptionDetails().recognizedOptions()).doesNotContainKey("t");
        //noinspection unchecked
        assertThat(route.getAllowedSenders()).containsExactly(Player.class);
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
        assertThat(route.getOptionDetails().recognizedOptions()).containsKey("?");
        assertThat(route.getOptionDetails().recognizedOptions()).doesNotContainKey("t");
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

    @Test
    public void test_parse_command_method_non_standard_help() throws NoSuchMethodException, CommandParseException
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithNonStandardHelp", CommandRequest.class);
        CommandRoute route = parser.parseCommandMethodAnnotation(method, this);

        assertThat(route.getOptionDetails().recognizedOptions()).containsKey("t");
        assertThat(route.getOptionDetails().recognizedOptions()).doesNotContainKey("?");
    }

    @Test
    public void test_fetch_parameters_from_parser()
    {
        OptionParser options = new OptionParser();
        options.accepts("a", "description"); //no arg shouldn't show up
        options.accepts("b").withOptionalArg(); //should show up as string
        options.accepts("c").withOptionalArg().ofType(Integer.class); //should show up as Integer
        options.accepts("d").withOptionalArg().withValuesConvertedBy(new OnlinePlayerValueConverter(true)); //should show up as Player
        options.accepts("e").withRequiredArg(); //should show up as string
        options.accepts("f").withRequiredArg().ofType(Integer.class); //should show up as Integer
        options.accepts("g").withRequiredArg().withValuesConvertedBy(new OnlinePlayerValueConverter(true)); //should show up as Player

        Map<String, Class> params = parser.getParameters(options);

        assertThat(params.size()).isEqualTo(7);
        assertThat(params.get("a")).isNull();
        assertThat(params.get("b")).isEqualTo(String.class);
        assertThat(params.get("c")).isEqualTo(Integer.class);
        assertThat(params.get("d")).isEqualTo(Player[].class);
        assertThat(params.get("e")).isEqualTo(String.class);
        assertThat(params.get("f")).isEqualTo(Integer.class);
        assertThat(params.get("g")).isEqualTo(Player[].class);
        assertThat(params.get("[arguments]")).isEqualTo(String[].class);

        options.nonOptions().withValuesConvertedBy(new OnlinePlayerValueConverter(true));
        params = parser.getParameters(options);

        assertThat(params.size()).isEqualTo(7);
        assertThat(params.get("[arguments]")).isEqualTo(Player[][].class);

        options.nonOptions().ofType(Double.class);
        params = parser.getParameters(options);

        assertThat(params.size()).isEqualTo(7);
        assertThat(params.get("[arguments]")).isEqualTo(Double[].class);
    }

    public void testArgumentCheckMethod(OptionSet set, CommandSender sender, Integer integer, Player[] player, Location[] arguments)
    {}

    public void testArgumentCheckMethodMissingDefault(Integer integer, Player[] player, Location[] arguments)
    {}

    @Test(expected = CommandParseException.class)
    public void test_argument_missing_defaults() throws NoSuchMethodException, CommandParseException
    {
        Method method = getClass().getDeclaredMethod("testArgumentCheckMethodMissingDefault", Integer.class, Player[].class, Location[].class);
        OptionParser p = new OptionParser();
        p.accepts("p")
                .withRequiredArg()
                .withValuesConvertedBy(new OnlinePlayerValueConverter(true));
        p.accepts("radius")
                .withOptionalArg()
                .ofType(Integer.class);
        p.accepts("t");
        p.nonOptions().withValuesConvertedBy(new LocationValueConverter());

        parser.getParameterPositions(method, p);
    }

    @Test
    public void test_argument_posistions() throws NoSuchMethodException, CommandParseException
    {
        Method method = getClass().getDeclaredMethod("testArgumentCheckMethod", OptionSet.class, CommandSender.class, Integer.class, Player[].class, Location[].class);
        OptionParser p = new OptionParser();
        p.accepts("p")
                .withRequiredArg()
                .withValuesConvertedBy(new OnlinePlayerValueConverter(true));
        p.accepts("radius")
                .withOptionalArg()
                .ofType(Integer.class);
        p.accepts("t");
        p.nonOptions().withValuesConvertedBy(new LocationValueConverter());

        parser.getParameterPositions(method, p);
    }

    @Test(expected = CommandParseException.class)
    public void test_argument_posistions_too_many() throws NoSuchMethodException, CommandParseException
    {
        Method method = getClass().getDeclaredMethod("testArgumentCheckMethod", OptionSet.class, CommandSender.class, Integer.class, Player[].class, Location[].class);
        OptionParser p = new OptionParser();
        p.accepts("p")
                .withRequiredArg()
                .withValuesConvertedBy(new OnlinePlayerValueConverter(true));
        //don't add the radius here, the signature should fail because it has an extra Integer
        p.accepts("t");
        p.nonOptions().withValuesConvertedBy(new LocationValueConverter());

        parser.getParameterPositions(method, p);
    }

    @Test(expected = CommandParseException.class)
    public void test_argument_posistions_not_enough() throws NoSuchMethodException, CommandParseException
    {
        Method method = getClass().getDeclaredMethod("testArgumentCheckMethod", OptionSet.class, CommandSender.class, Integer.class, Player[].class, Location[].class);
        OptionParser p = new OptionParser();
        p.accepts("p")
                .withRequiredArg()
                .withValuesConvertedBy(new OnlinePlayerValueConverter(true));
        p.accepts("radius")
                .withOptionalArg()
                .ofType(Integer.class);
        p.accepts("minradius")
                .withOptionalArg()
                .ofType(Integer.class); //adding this means the signature doesn't have enough integers in it now
        p.accepts("t");
        p.nonOptions().withValuesConvertedBy(new LocationValueConverter());

        parser.getParameterPositions(method, p);
    }

    public void testOptionPosistion(OptionSet set, Location ex, String param, Double r, Player[][] players)
    {}

    private Method getTestOptionPosistionMethod() throws NoSuchMethodException
    {
        return getClass().getDeclaredMethod("testOptionPosistion", OptionSet.class, Location.class, String.class, Double.class, Player[][].class);
    }

    private OptionParser getParserForOptionPosistionMethod()
    {
        OptionParser p = new OptionParser();
        p.accepts("l")
                .withOptionalArg()
                .withValuesConvertedBy(new LocationValueConverter());
        p.accepts("string")
                .withRequiredArg();
        p.accepts("radius")
                .withRequiredArg()
                .ofType(Double.class);
        p.nonOptions().withValuesConvertedBy(new OnlinePlayerValueConverter(true));
        return p;
    }

    @Test
    public void test_option_posistion_correct() throws NoSuchMethodException, CommandParseException
    {
        Method method = getTestOptionPosistionMethod();
        OptionParser p = getParserForOptionPosistionMethod();


        String[] posistion = new String[]{"l", "string", "radius", "[arguments]"};
        parser.checkPositionsCorrect(method, posistion, p, 1);
    }

    @Test(expected = CommandParseException.class)
    public void test_option_posistion_wrong_offset() throws NoSuchMethodException, CommandParseException
    {
        Method method = getTestOptionPosistionMethod();
        OptionParser p = getParserForOptionPosistionMethod();


        String[] posistion = new String[]{"l", "string", "radius", "[arguments]"};
        parser.checkPositionsCorrect(method, posistion, p, 2); //wrong offset so now the arg list is all wrong
    }

    @Test(expected = CommandParseException.class)
    public void test_option_posistions_too_many() throws NoSuchMethodException, CommandParseException
    {
        Method method = getTestOptionPosistionMethod();
        OptionParser p = getParserForOptionPosistionMethod();

        String[] posistion = new String[]{"l", "string", "radius", "[arguments]", "l"}; //added extra 'l', method doesn't have correct param count now
        parser.checkPositionsCorrect(method, posistion, p, 1);
    }

    @Test(expected = CommandParseException.class)
    public void test_option_posistions_not_enough() throws NoSuchMethodException, CommandParseException
    {
        Method method = getTestOptionPosistionMethod();
        OptionParser p = getParserForOptionPosistionMethod();

        String[] posistion = new String[]{"l", "string", "radius"}; //removed the [arguments], method doesn't have correct param count now
        parser.checkPositionsCorrect(method, posistion, p, 1);
    }

    @Test(expected = CommandParseException.class)
    public void test_option_posistions_wrong_class() throws NoSuchMethodException, CommandParseException
    {
        Method method = getTestOptionPosistionMethod();
        OptionParser p = getParserForOptionPosistionMethod();

        String[] posistion = new String[]{"l", "radius", "string", "[arguments]"}; //switch string and radius so class don't match now
        parser.checkPositionsCorrect(method, posistion, p, 1);
    }

    @Test(expected = CommandParseException.class)
    public void test_option_posistions_invalid_option() throws NoSuchMethodException, CommandParseException
    {
        Method method = getTestOptionPosistionMethod();
        OptionParser p = getParserForOptionPosistionMethod();

        String[] posistion = new String[]{"l", "strings", "radius", "[arguments]"}; //switch string with strings which is an invalid option
        parser.checkPositionsCorrect(method, posistion, p, 1);
    }
}
