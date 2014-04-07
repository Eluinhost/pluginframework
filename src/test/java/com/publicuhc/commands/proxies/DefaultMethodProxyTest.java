package com.publicuhc.commands.proxies;

import com.publicuhc.commands.requests.SenderType;
import org.bukkit.command.Command;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
public class DefaultMethodProxyTest {

    private DefaultMethodProxy proxy;

    @Test
    public void testDoParamsMatch(){

        proxy.setPattern(Pattern.compile("[\\d]++"));
        assertFalse(proxy.doParamsMatch("e09=324"));
        assertTrue(proxy.doParamsMatch("3908420"));

        proxy.setPattern(Pattern.compile("[\\D]++"));
        assertFalse(proxy.doParamsMatch("3908420"));
        assertTrue(proxy.doParamsMatch("eff=gdk"));
    }

    @Test
    public void testBaseCommand() {
        Command command = mock(Command.class);
        proxy.setBaseCommand(command);

        assertThat(proxy.getBaseCommand(), is(sameInstance(command)));
    }

    @Test
    public void testPattern() {
        Pattern pattern = mock(Pattern.class);
        proxy.setPattern(pattern);

        assertThat(proxy.getPattern(), is(sameInstance(pattern)));
    }

    @Test
    public void testPermission() {
        proxy.setPermission("perm");

        assertThat(proxy.getPermission(), is(equalTo("perm")));
    }

    @Test
    public void testAllowedSenders() {
        SenderType[] allowed = {SenderType.COMMAND_BLOCK, SenderType.CONSOLE};
        proxy.setAllowedSenders(allowed);

        assertThat(proxy.getAllowedSenders(), is(equalTo(allowed)));
    }

    @Before
    public void onStartUp(){
        proxy = new DefaultMethodProxy();
    }
}
