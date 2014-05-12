/*
 * DefaultCommandRequestBuilderTest.java
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

package com.publicuhc.pluginframework.commands.requests;

import com.publicuhc.pluginframework.translate.Translate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class DefaultCommandRequestBuilderTest {

    private DefaultCommandRequestBuilder builder;

    @Test
    public void testCommand() {
        Command command = mock(Command.class);

        assertThat(builder.setCommand(command)).isSameAs(builder);
        assertThat(builder.getCommand()).isSameAs(command);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCommand() {
        builder.setCommand(null);
    }

    @Test
    public void testCount() {
        assertThat(builder.setCount(10)).isSameAs(builder);
        assertThat(builder.getCount()).isEqualTo(10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCount() {
        builder.setCount(-1);
    }

    @Test
    public void testArguments() {
        String[] args = {"arg1", "arg2", "arg3"};

        assertThat(builder.setArguments(args)).isSameAs(builder);
        assertThat(builder.getArguments()).contains(args);
    }

    @Test
    public void testArgumentsList() {
        List<String> args = Arrays.asList("arg1", "arg2", "arg3");

        assertThat(builder.setArguments(args)).isSameAs(builder);
        assertThat(builder.getArguments()).containsAll(args);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullArguments() {
        String[] args = null;
        builder.setArguments(args);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullArgumentsList() {
        List<String> args = null;
        builder.setArguments(args);
    }

    @Test
    public void testCommandSender() {
        CommandSender sender = mock(CommandSender.class);

        assertThat(builder.setSender(sender)).isSameAs(builder);
        assertThat(builder.getSender()).isSameAs(sender);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCommandSender() {
        builder.setSender(null);
    }

    @Test
    public void testIsValid() {
        assertThat(builder.isValid()).isFalse();
        builder.setSender(mock(CommandSender.class));
        assertThat(builder.isValid()).isFalse();
        builder.setArguments(new String[]{});
        assertThat(builder.isValid()).isFalse();
        builder.setCommand(mock(Command.class));
        assertThat(builder.isValid()).isFalse();
        builder.setCount(1);
        assertThat(builder.isValid()).isTrue();
    }

    @Test
    public void testClear() {
        builder.setSender(mock(CommandSender.class));
        builder.setArguments(new String[]{});
        builder.setCommand(mock(Command.class));

        builder.clear();
        assertThat(builder.getSender()).isNull();
        assertThat(builder.getArguments()).isNull();
        assertThat(builder.getCommand()).isNull();
    }

    @Test
    public void testBuild() {
        CommandSender sender = mock(CommandSender.class);
        String[] args = {"arg1", "arg2", "arg3"};
        Command command = mock(Command.class);

        builder.setSender(sender);
        builder.setArguments(args);
        builder.setCommand(command);
        builder.setCount(1);

        CommandRequest request = builder.build();

        assertThat(request.getArgs()).contains(args);
        assertThat(request.getCommand()).isSameAs(command);
        assertThat(request.getSender()).isSameAs(sender);
        assertThat(request.getCount()).isEqualTo(1);
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidBuild() {
        builder.build();
    }

    @Before
    public void onStartUp() {
        Translate translate = mock(Translate.class);
        when(translate.getLocaleForSender(any(CommandSender.class))).thenReturn("en");
        builder = new DefaultCommandRequestBuilder(translate);
    }
}
