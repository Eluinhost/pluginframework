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

package com.publicuhc.commands.requests;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
public class DefaultCommandRequestBuilderTest {

    private DefaultCommandRequestBuilder builder;

    @Test
    public void testCommand() {
        Command command = mock(Command.class);

        assertThat(builder.setCommand(command), is(CoreMatchers.<CommandRequestBuilder>sameInstance(builder)));
        assertThat(builder.getCommand(), is(sameInstance(command)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCommand() {
        builder.setCommand(null);
    }

    @Test
    public void testMatchResult() {
        MatchResult result = mock(MatchResult.class);

        assertThat(builder.setMatchResult(result), is(CoreMatchers.<CommandRequestBuilder>sameInstance(builder)));
        assertThat(builder.getMatchResult(), is(sameInstance(result)));
    }

    @Test
    public void testArguments() {
        String[] args = {"arg1", "arg2", "arg3"};

        assertThat(builder.setArguments(args), is(CoreMatchers.<CommandRequestBuilder>sameInstance(builder)));
        assertThat(builder.getArguments(), is(equalTo(Arrays.asList(args))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullMatchResult() {
        builder.setMatchResult(null);
    }

    @Test
    public void testArgumentsList() {
        List<String> args = Arrays.asList("arg1", "arg2", "arg3");

        assertThat(builder.setArguments(args), is(CoreMatchers.<CommandRequestBuilder>sameInstance(builder)));
        assertThat(builder.getArguments(), is(equalTo(args)));
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

        assertThat(builder.setSender(sender), is(CoreMatchers.<CommandRequestBuilder>sameInstance(builder)));
        assertThat(builder.getSender(), is(sameInstance(sender)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCommandSender() {
        builder.setSender(null);
    }

    @Test
    public void testIsValid(){
        assertFalse(builder.isValid());
        builder.setSender(mock(CommandSender.class));
        assertFalse(builder.isValid());
        builder.setArguments(new String[]{});
        assertFalse(builder.isValid());
        builder.setCommand(mock(Command.class));
        assertFalse(builder.isValid());
        builder.setMatchResult(mock(MatchResult.class));
        assertTrue(builder.isValid());
    }

    @Test
    public void testClear() {
        builder.setSender(mock(CommandSender.class));
        builder.setArguments(new String[]{});
        builder.setCommand(mock(Command.class));
        builder.setMatchResult(mock(MatchResult.class));

        builder.clear();
        assertThat(builder.getSender(), is(nullValue()));
        assertThat(builder.getArguments(), is(nullValue()));
        assertThat(builder.getCommand(), is(nullValue()));
        assertThat(builder.getMatchResult(), is(nullValue()));
    }

    @Test
    public void testBuild() {
        CommandSender sender = mock(CommandSender.class);
        String[] args = {"arg1", "arg2", "arg3"};
        Command command = mock(Command.class);
        MatchResult result = mock(MatchResult.class);

        builder.setSender(sender);
        builder.setArguments(args);
        builder.setCommand(command);
        builder.setMatchResult(result);

        CommandRequest request = builder.build();

        assertThat(request.getArgs(), is(equalTo(Arrays.asList(args))));
        assertThat(request.getCommand(), is(sameInstance(command)));
        assertThat(request.getMatcherResult(), is(sameInstance(result)));
        assertThat(request.getSender(), is(sameInstance(sender)));
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidBuild() {
        builder.build();
    }

    @Before
    public void onStartUp() {
        builder = new DefaultCommandRequestBuilder();
    }
}
