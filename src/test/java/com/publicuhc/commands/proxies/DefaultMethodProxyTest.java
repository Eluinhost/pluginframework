package com.publicuhc.commands.proxies;

import com.publicuhc.commands.requests.SenderType;
import org.bukkit.command.Command;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
public class DefaultMethodProxyTest {

    private DefaultMethodProxy proxy;

    @Test
    public void testDoParamsMatch(){

        proxy.setPattern(Pattern.compile("[\\d]++"));
        assertThat(proxy.paramsMatch("e09=324"), is(nullValue()));
        assertThat(proxy.paramsMatch("3908420"), is(not(nullValue())));

        proxy.setPattern(Pattern.compile("[\\D]++"));
        assertThat(proxy.paramsMatch("3908420"), is(nullValue()));
        assertThat(proxy.paramsMatch("eff=gdk"), is(not(nullValue())));
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
