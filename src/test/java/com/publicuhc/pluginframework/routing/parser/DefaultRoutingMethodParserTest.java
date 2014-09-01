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

import com.publicuhc.pluginframework.routing.CommandRoute;
import com.publicuhc.pluginframework.routing.converters.OnlinePlayerValueConverter;
import com.publicuhc.pluginframework.routing.exception.CommandParseException;
import com.publicuhc.pluginframework.routing.tester.CommandTester;
import com.publicuhc.pluginframework.routing.tester.PermissionTester;
import com.publicuhc.pluginframework.routing.tester.SenderTester;
import joptsimple.OptionParser;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
public class DefaultRoutingMethodParserTest
{
    private DefaultRoutingMethodParser parser;
    private TestMethods testMethods;

    @Before
    public void onStartup()
    {
        parser = new DefaultRoutingMethodParser();
        testMethods = spy(new TestMethods());
    }
    @Test
    public void test_parse_with_permissions() throws NoSuchMethodException, CommandParseException
    {
        Method method = testMethods.getMethodWithPermission();

        CommandRoute route = parser.parseCommandMethodAnnotation(method, testMethods);

        CommandTester tester = route.getTesters().get(0);
        assertThat(tester).isInstanceOf(PermissionTester.class);

        PermissionTester pTester = (PermissionTester) tester;
        assertThat(pTester).containsExactly("TEST.PERMISSION");
        assertThat(pTester.isMatchingAll()).isTrue();

        method = testMethods.getMethod();
        route = parser.parseCommandMethodAnnotation(method, testMethods);

        tester = route.getTesters().get(0);
        assertThat(tester).isInstanceOf(PermissionTester.class);

        pTester = (PermissionTester) tester;
        assertThat(pTester).isEmpty();
        assertThat(pTester.isMatchingAll()).isTrue();
    }

    @Test
    public void test_parse_options() throws NoSuchMethodException, CommandParseException
    {
        Method method = testMethods.getMethodWithOptions();
        CommandRoute route = parser.parseCommandMethodAnnotation(method, testMethods);
        assertThat(route.getOptionDetails().recognizedOptions().keySet()).containsOnly("a", "b", "?", "[arguments]"); //will have the ? and [arguments] automatically added

        method = testMethods.getMethod();
        route = parser.parseCommandMethodAnnotation(method, testMethods);
        assertThat(route.getOptionDetails().recognizedOptions().keySet()).containsOnly("?", "[arguments]"); //will have the ? and [arguments] automatically added
    }

    @Test
    public void test_starts_with() throws NoSuchMethodException, CommandParseException
    {
        Method method = testMethods.getMethodSubcommand();
        CommandRoute route = parser.parseCommandMethodAnnotation(method, testMethods);
        assertThat(route.getStartsWith()).containsExactly("subcommand");

        method = testMethods.getMethod();
        route = parser.parseCommandMethodAnnotation(method, testMethods);
        assertThat(route.getStartsWith().length).isEqualTo(0);
    }

    @Test
    public void test_parse_method_chosen_senders() throws NoSuchMethodException, CommandParseException
    {
        Method method = testMethods.getMethodWithChosenSenders();
        CommandRoute route = parser.parseCommandMethodAnnotation(method, testMethods);

        CommandTester commandTester = route.getTesters().get(1);
        assertThat(commandTester).isInstanceOf(SenderTester.class);

        SenderTester tester = (SenderTester) commandTester;
        //noinspection unchecked
        assertThat(tester).containsOnly(ConsoleCommandSender.class, Player.class);

        method = testMethods.getMethod();
        route = parser.parseCommandMethodAnnotation(method, testMethods);
        commandTester = route.getTesters().get(1);
        assertThat(commandTester).isInstanceOf(SenderTester.class);

        tester = (SenderTester) commandTester;
        //noinspection unchecked
        assertThat(tester).containsExactly(CommandSender.class);
    }

    @Test(expected = CommandParseException.class)
    public void test_parse_method_options_method_missing() throws CommandParseException, NoSuchMethodException
    {
        Method method = testMethods.getMethodWithOptionsMissing();
        parser.parseCommandMethodAnnotation(method, testMethods);
    }

    @Test(expected = CommandParseException.class)
    public void test_parse_method_options_method_invalid_parameters() throws NoSuchMethodException, CommandParseException
    {
        Method method = testMethods.getMethodWithOptionsWithInvalidParam();
        parser.parseCommandMethodAnnotation(method, testMethods);
    }

    @Test(expected = CommandParseException.class)
    public void test_parse_method_no_annotation() throws NoSuchMethodException, CommandParseException
    {
        Method method = testMethods.getMethodWithoutAnnotation();
        parser.parseCommandMethodAnnotation(method, testMethods);
    }

    @Test(expected = CommandParseException.class)
    public void test_parse_command_method_invalid_arg_1() throws NoSuchMethodException, CommandParseException
    {
        Method method = testMethods.getMethodWithInvalidParam1();
        parser.parseCommandMethodAnnotation(method, testMethods);
    }

    @Test(expected = CommandParseException.class)
    public void test_parse_command_method_invalid_arg_2() throws NoSuchMethodException, CommandParseException
    {
        Method method = testMethods.getMethodWithInvalidParam2();
        parser.parseCommandMethodAnnotation(method, testMethods);
    }

    @Test(expected = CommandParseException.class)
    public void test_parse_command_method_unmatched_sender() throws NoSuchMethodException, CommandParseException
    {
        Method method = testMethods.getMethodWithUnmatchedSender();
        parser.parseCommandMethodAnnotation(method, testMethods);
    }

    @Test()
    public void test_parse_command_method_matched_sender() throws NoSuchMethodException, CommandParseException
    {
        Method method = testMethods.getMethodWithMatchedSender();
        parser.parseCommandMethodAnnotation(method, testMethods);
    }

    @Test
    public void test_parse_command_method_non_standard_help() throws NoSuchMethodException, CommandParseException
    {
        Method method = testMethods.getMethodWithNonStandardHelp();
        CommandRoute route = parser.parseCommandMethodAnnotation(method, testMethods);
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
        assertThat(params.get("[arguments]")).isEqualTo(List.class);

        options.nonOptions().withValuesConvertedBy(new OnlinePlayerValueConverter(true));
        params = parser.getParameters(options);

        assertThat(params.size()).isEqualTo(7);
        assertThat(params.get("[arguments]")).isEqualTo(List.class);

        options.nonOptions().ofType(Double.class);
        params = parser.getParameters(options);

        assertThat(params.size()).isEqualTo(7);
        assertThat(params.get("[arguments]")).isEqualTo(List.class);
    }

    @Test
    public void test_option_posistion_correct() throws NoSuchMethodException, CommandParseException
    {
        Method method = testMethods.getTestOptionsPosistions();
        OptionParser p = testMethods.getParserForOptionPosistionMethod();

        String[] posistion = new String[]{"l", "string", "radius", "[arguments]"};
        parser.checkPositionsCorrect(method, posistion, p, 2);
    }

    @Test(expected = CommandParseException.class)
    public void test_option_posistion_wrong_offset() throws NoSuchMethodException, CommandParseException
    {
        Method method = testMethods.getTestOptionsPosistions();
        OptionParser p = testMethods.getParserForOptionPosistionMethod();

        String[] posistion = new String[]{"l", "string", "radius", "[arguments]"};
        parser.checkPositionsCorrect(method, posistion, p, 3); //wrong offset so now the arg list is all wrong
    }

    @Test(expected = CommandParseException.class)
    public void test_option_posistions_too_many() throws NoSuchMethodException, CommandParseException
    {
        Method method = testMethods.getTestOptionsPosistions();
        OptionParser p = testMethods.getParserForOptionPosistionMethod();

        String[] posistion = new String[]{"l", "string", "radius", "[arguments]", "l"}; //added extra 'l', method doesn't have correct param count now
        parser.checkPositionsCorrect(method, posistion, p, 2);
    }

    @Test(expected = CommandParseException.class)
    public void test_option_posistions_not_enough() throws NoSuchMethodException, CommandParseException
    {
        Method method = testMethods.getTestOptionsPosistions();
        OptionParser p = testMethods.getParserForOptionPosistionMethod();

        String[] posistion = new String[]{"l", "string", "radius"}; //removed the [arguments], method doesn't have correct param count now
        parser.checkPositionsCorrect(method, posistion, p, 2);
    }

    @Test(expected = CommandParseException.class)
    public void test_option_posistions_wrong_class() throws NoSuchMethodException, CommandParseException
    {
        Method method = testMethods.getTestOptionsPosistions();
        OptionParser p = testMethods.getParserForOptionPosistionMethod();

        String[] posistion = new String[]{"l", "radius", "string", "[arguments]"}; //switch string and radius so class don't match now
        parser.checkPositionsCorrect(method, posistion, p, 2);
    }

    @Test(expected = CommandParseException.class)
    public void test_option_posistions_invalid_option() throws NoSuchMethodException, CommandParseException
    {
        Method method = testMethods.getTestOptionsPosistions();
        OptionParser p = testMethods.getParserForOptionPosistionMethod();

        String[] posistion = new String[]{"l", "strings", "radius", "[arguments]"}; //switch string with strings which is an invalid option
        parser.checkPositionsCorrect(method, posistion, p, 2);
    }

    @Test
    public void test_list_type_correct()
    {
        OptionParser p = new OptionParser();
        p.accepts("a")
                .withRequiredArg()
                .ofType(Double.class)
                .withValuesSeparatedBy(",");
        p.accepts("b")
                .withOptionalArg();
        Map<String, Class> map = parser.getParameters(p);

        assertThat(map.get("a")).isEqualTo(List.class);
        assertThat(map.get("b")).isEqualTo(String.class);
        assertThat(map.get("[arguments]")).isEqualTo(List.class);
        assertThat(map.keySet()).containsOnly("a", "b", "[arguments]");
    }
}
