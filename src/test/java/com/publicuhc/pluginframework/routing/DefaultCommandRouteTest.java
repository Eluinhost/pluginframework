package com.publicuhc.pluginframework.routing;

import com.publicuhc.pluginframework.routing.exception.CommandInvocationException;
import com.publicuhc.pluginframework.routing.proxy.ReflectionMethodProxy;
import com.publicuhc.pluginframework.routing.proxy.MethodProxy;
import junit.framework.AssertionFailedError;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.OptionParser;
import org.bukkit.craftbukkit.libs.joptsimple.OptionSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
public class DefaultCommandRouteTest
{
    private OptionParser parser;
    private TestClass testObject;

    @Before
    public void onStartup() throws NoSuchMethodException
    {
        testObject = mock(TestClass.class);
        parser = mock(OptionParser.class);
    }

    @Test
    public void testValidInvocation() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, CommandInvocationException
    {
        Method method = TestClass.class.getMethod("testMethod", Command.class, CommandSender.class, OptionSet.class);
        MethodProxy proxy = spy(new ReflectionMethodProxy(testObject, method));
        parser = mock(OptionParser.class);
        DefaultCommandRoute route = new DefaultCommandRoute("test", proxy, parser);

        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = new String[]{"1", "2"};

        route.run(command, sender, args);

        verify(parser, times(1)).parse(args);
        verify(proxy, times(1)).invoke(same(command), same(sender), any(OptionSet.class));
        verify(testObject, times(1)).testMethod(same(command), same(sender), any(OptionSet.class));
    }

    @Test
    public void testExceptionInvocation() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method method = TestClass.class.getMethod("testExceptionMethod", Command.class, CommandSender.class, OptionSet.class);
        MethodProxy proxy = spy(new ReflectionMethodProxy(testObject, method));
        parser = mock(OptionParser.class);
        DefaultCommandRoute route = new DefaultCommandRoute("test", proxy, parser);

        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = new String[]{"1", "2"};

        try {
            route.run(command, sender, args);
            verify(parser, times(1)).parse(args);
            verify(proxy, times(1)).invoke(same(command), same(sender), any(OptionSet.class));
            verify(testObject, times(1)).testExceptionMethod(same(command), same(sender), any(OptionSet.class));
            return;
        } catch(CommandInvocationException ignored) {}
        throw new AssertionFailedError("Expected CommandInvocationException");
    }

    private class TestClass
    {
        public static final String TEST_STRING = "TEST STRING";
        public String testMethod(Command command, CommandSender sender, OptionSet set)
        {
            return TEST_STRING;
        }
        public String testExceptionMethod(Command command, CommandSender sender, OptionSet set)
        {
            throw new IllegalStateException();
        }
    }
}
