package com.publicuhc.pluginframework.routing;

import com.publicuhc.pluginframework.routing.exception.AnnotationMissingException;
import com.publicuhc.pluginframework.routing.exception.CommandParseException;
import com.publicuhc.pluginframework.routing.proxy.MethodProxy;
import junit.framework.AssertionFailedError;
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
    public String commandWithAnnotation()
    {
        return "TEST_STRING";
    }

    public void commandWithAnnotation(OptionParser optionParser)
    {
        optionParser.accepts("a").withRequiredArg().ofType(Integer.class);
        optionParser.accepts("b").withOptionalArg().ofType(String.class);
    }

    public void commandWithoutAnnotation()
    {}

    @CommandMethod(command = "test", options = "a:b::")
    public String commandWithAnnotationOptions()
    {
        return "TEST_STRING";
    }

    @CommandMethod(command = "test", options = "a:b::**")
    public void commandWithInvalidAnnotationOptions()
    {}

    @CommandMethod(command = "test")
    public void commandWithAnnotationButNoOptions()
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
        Method valid = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotation");
        assertThat(parser.hasCommandMethodAnnotation(valid)).isTrue();

        Method invalid = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithoutAnnotation");
        assertThat(parser.hasCommandMethodAnnotation(invalid)).isFalse();
    }

    @Test
    public void test_get_options_method() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotation");
        OptionParser optionParser = parser.getOptionsForMethod(method, this);
        assertOptionsValid(optionParser);
    }

    @Test
    public void test_parse_method_options_method() throws Throwable
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotation");
        CommandRoute route = parser.parseCommandMethodAnnotation(method, this);

        assertThat(route.getCommandName()).isEqualTo("test");
        assertOptionsValid(route.getOptionDetails());

        MethodProxy proxy = route.getProxy();
        assertThat(proxy.getInstance()).isSameAs(this);
        assertThat(proxy.invoke()).isEqualTo("TEST_STRING");
    }

    @Test
    public void test_parse_method_options_method_missing() throws NoSuchMethodException
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotationButNoOptions");

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
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithoutAnnotation");

        try {
            parser.parseCommandMethodAnnotation(method, this);
            throw new AssertionFailedError("Expected CommandParseException");
        } catch (CommandParseException ex) {
            assertThat(ex).isInstanceOf(AnnotationMissingException.class);
        }
    }

    @Test
    public void test_parse_method_annotation_with_options() throws Throwable
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithAnnotationOptions");

        CommandRoute route = parser.parseCommandMethodAnnotation(method, this);

        assertThat(route.getCommandName()).isEqualTo("test");
        assertOptionsValidStrings(route.getOptionDetails());
        assertThat(route.getProxy().invoke()).isEqualTo("TEST_STRING");
    }

    @Test
    public void test_parse_method_annotation_with_invalid_options() throws Throwable
    {
        Method method = DefaultRoutingMethodParserTest.class.getDeclaredMethod("commandWithInvalidAnnotationOptions");

        try {
            parser.parseCommandMethodAnnotation(method, this);
            throw new AssertionFailedError("Expected CommandParseException");
        } catch(CommandParseException ex) {
            assertThat(ex.getCause()).isInstanceOf(OptionException.class);
        }
    }
}
