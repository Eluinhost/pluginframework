package com.publicuhc.pluginframework.routing;

import com.publicuhc.pluginframework.routing.exception.AnnotationMissingException;
import com.publicuhc.pluginframework.routing.exception.CommandParseException;
import com.publicuhc.pluginframework.routing.proxy.MethodProxy;
import junit.framework.AssertionFailedError;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
    @CommandMethod(command = "test")
    public String commandWithAnnotation(Command command, CommandSender sender, OptionSet set)
    {
        return "TEST_STRING";
    }

    public void commandWithAnnotation(OptionParser optionParser)
    {
        optionParser.accepts("a").withRequiredArg().ofType(Integer.class);
        optionParser.accepts("b").withOptionalArg().ofType(String.class);
    }

    public void commandWithoutAnnotation(Command command, CommandSender sender, OptionSet set)
    {}

    @CommandMethod(command = "test", options = "a:b::")
    public String commandWithAnnotationOptions(Command command, CommandSender sender, OptionSet set)
    {
        return "TEST_STRING";
    }

    @CommandMethod(command = "test", options = "a:b::**")
    public void commandWithInvalidAnnotationOptions(Command command, CommandSender sender, OptionSet set)
    {}

    @CommandMethod(command = "test")
    public void commandWithAnnotationButNoOptions(Command command, CommandSender sender, OptionSet set)
    {}

    @CommandMethod(command = "test")
    public void commandWithExtraArgs(Command command, CommandSender sender, OptionSet set, String[] extra)
    {}

    @CommandMethod(command = "test")
    public void commandWithNotEnoughArgs(Command command, CommandSender sender)
    {}

    @CommandMethod(command = "test")
    public void commandWithWrongArg1(CommandBlock command, CommandSender sender, OptionSet set)
    {}

    @CommandMethod(command = "test")
    public void commandWithWrongArg2(Command command, Command sender, OptionSet set)
    {}

    @CommandMethod(command = "test")
    public void commandWithWrongArg3(Command command, CommandSender sender, OptionParser set)
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
    }

    //actual tests
    @Test
    public void test_has_command_method_annotation() throws NoSuchMethodException
    {
        Method valid = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotation", Command.class, CommandSender.class, OptionSet.class);
        assertThat(parser.hasCommandMethodAnnotation(valid)).isTrue();

        Method invalid = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithoutAnnotation", Command.class, CommandSender.class, OptionSet.class);
        assertThat(parser.hasCommandMethodAnnotation(invalid)).isFalse();
    }

    @Test
    public void test_get_options_method() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotation", Command.class, CommandSender.class, OptionSet.class);
        OptionParser optionParser = parser.getOptionsForMethod(method, this);
        assertOptionsValid(optionParser);
    }

    @Test
    public void test_parse_method_options_method() throws Throwable
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotation", Command.class, CommandSender.class, OptionSet.class);
        CommandRoute route = parser.parseCommandMethodAnnotation(method, this);

        assertThat(route.getCommandName()).isEqualTo("test");
        assertOptionsValid(route.getOptionDetails());

        MethodProxy proxy = route.getProxy();
        assertThat(proxy.getInstance()).isSameAs(this);
        assertThat(proxy.invoke(mock(Command.class), mock(CommandSender.class), mock(OptionSet.class))).isEqualTo("TEST_STRING");
    }

    @Test
    public void test_parse_method_options_method_missing() throws NoSuchMethodException
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotationButNoOptions", Command.class, CommandSender.class, OptionSet.class);

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
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithoutAnnotation", Command.class, CommandSender.class, OptionSet.class);

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
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotationOptions", Command.class, CommandSender.class, OptionSet.class);

        CommandRoute route = parser.parseCommandMethodAnnotation(method, this);

        assertThat(route.getCommandName()).isEqualTo("test");
        assertOptionsValidStrings(route.getOptionDetails());
        assertThat(route.getProxy().invoke(mock(Command.class), mock(CommandSender.class), mock(OptionSet.class))).isEqualTo("TEST_STRING");
    }

    @Test
    public void test_parse_method_annotation_with_invalid_options() throws Throwable
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithInvalidAnnotationOptions", Command.class, CommandSender.class, OptionSet.class);

        try {
            parser.parseCommandMethodAnnotation(method, this);
            throw new AssertionFailedError("Expected CommandParseException");
        } catch(CommandParseException ex) {
            assertThat(ex.getCause()).isInstanceOf(OptionException.class);
        }
    }

    @Test
    public void test_command_method_parameters_correct() throws NoSuchMethodException
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithExtraArgs", Command.class, CommandSender.class, OptionSet.class, String[].class);
        assertThat(parser.areCommandMethodParametersCorrect(method)).isFalse();

        method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithNotEnoughArgs", Command.class, CommandSender.class);
        assertThat(parser.areCommandMethodParametersCorrect(method)).isFalse();

        method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithWrongArg1", CommandBlock.class, CommandSender.class, OptionSet.class);
        assertThat(parser.areCommandMethodParametersCorrect(method)).isFalse();

        method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithWrongArg2", Command.class, Command.class, OptionSet.class);
        assertThat(parser.areCommandMethodParametersCorrect(method)).isFalse();

        method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithWrongArg3", Command.class, CommandSender.class, OptionParser.class);
        assertThat(parser.areCommandMethodParametersCorrect(method)).isFalse();

        method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotation", Command.class, CommandSender.class, OptionSet.class);
        assertThat(parser.areCommandMethodParametersCorrect(method)).isTrue();
    }

    @Test
    public void test_parse_command_method_invalid_args() throws NoSuchMethodException
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithExtraArgs", Command.class, CommandSender.class, OptionSet.class, String[].class);

        try {
            parser.parseCommandMethodAnnotation(method, this);
            throw new AssertionFailedError("Expected CommandParseException");
        } catch(CommandParseException ex) {
            assertThat(ex.getMessage()).contains("Invalid command method parameters");
        }
    }
}
