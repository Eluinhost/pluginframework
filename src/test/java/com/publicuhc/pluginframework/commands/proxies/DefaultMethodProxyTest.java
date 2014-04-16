/*
 * DefaultMethodProxyTest.java
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

package com.publicuhc.pluginframework.commands.proxies;

import com.publicuhc.pluginframework.commands.requests.SenderType;
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
    public void testDoParamsMatch() {

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
    public void onStartUp() {
        proxy = new DefaultMethodProxy();
    }
}
