package com.publicuhc.pluginframework.routing;

import com.publicuhc.pluginframework.routing.exception.CommandInvocationException;
import com.publicuhc.pluginframework.routing.proxy.MethodProxy;
import com.publicuhc.pluginframework.routing.proxy.ReflectionMethodProxy;
import junit.framework.AssertionFailedError;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.OptionSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
public class DefaultCommandRouteTest
{
    private CommandOptionsParser parser;
    private TestClass testObject;

    @Before
    public void onStartup() throws NoSuchMethodException
    {
        testObject = new TestClass();
        parser = mock(CommandOptionsParser.class);
    }

    @Test
    public void test_valid_invocation() throws Throwable
    {
        Method method = TestClass.class.getMethod("testMethod", Command.class, CommandSender.class, OptionSet.class);
        MethodProxy proxy = spy(new ReflectionMethodProxy<TestClass>(testObject, method));
        DefaultCommandRoute route = new DefaultCommandRoute("test", proxy, parser);

        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = new String[]{"1", "2"};

        try {
            route.run(command, sender, args);
        } catch(CommandInvocationException ex) {
            throw ex.getCause();
        }
        verify(parser, times(1)).parse(args);
        verify(proxy, times(1)).invoke(same(command), same(sender), any(OptionSet.class));

        assertThat(testObject.wasRan()).isTrue();
    }

    @Test
    public void test_invocation_with_exception() throws Throwable
    {
        Method method = TestClass.class.getMethod("exceptionMethod", Command.class, CommandSender.class, OptionSet.class);
        MethodProxy proxy = spy(new ReflectionMethodProxy<TestClass>(testObject, method));
        DefaultCommandRoute route = new DefaultCommandRoute("test", proxy, parser);

        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = new String[]{"1", "2"};

        try {
            route.run(command, sender, args);
            throw new AssertionFailedError("Expected CommandInvocationException");
        } catch(CommandInvocationException ignored) {
            verify(parser, times(1)).parse(args);
            verify(proxy, times(1)).invoke(same(command), same(sender), any(OptionSet.class));
            assertThat(testObject.wasRan()).isFalse();
        }
    }

    public static class TestClass
    {
        private boolean ran = false;

        public void testMethod(Command command, CommandSender sender, OptionSet set)
        {
            setRan(true);
        }

        public void exceptionMethod(Command command, CommandSender sender, OptionSet set)
        {
            throw new IllegalStateException();
        }

        public void setRan(boolean ran)
        {
            this.ran = ran;
        }

        public boolean wasRan()
        {
            return ran;
        }
    }
}
