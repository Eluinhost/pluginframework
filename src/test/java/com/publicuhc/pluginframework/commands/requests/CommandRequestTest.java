/*
 * CommandRequestTest.java
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

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class CommandRequestTest {

    private CommandRequest request;
    private CommandSender sender;
    private Command command;
    private MatchResult result;

    @Test
    public void testRemoveFirstArg() {
        assertThat(request.getArg(0), is(equalTo("first")));

        int length = request.getArgs().size();
        request.removeFirstArg();

        assertThat(request.getArgs().size(), is(equalTo(length - 1)));
        assertThat(request.getArg(0), not(equalTo("first")));
    }

    @Test
    public void testRemoveFirstArgNoArgs() {
        List<String> args = new ArrayList<String>();
        CommandSender sender = mock(CommandSender.class);
        Command command = mock(Command.class);
        MatchResult result = mock(MatchResult.class);
        CommandRequest request = new CommandRequest(command, args, sender, result, 1);

        request.removeFirstArg();
    }

    @Test
    public void testGetFirstArg() {
        assertThat(request.getFirstArg(), is(equalTo("first")));
    }

    @Test
    public void testGetFirstArgNoArgs() {
        List<String> args = new ArrayList<String>();
        CommandSender sender = mock(CommandSender.class);
        Command command = mock(Command.class);
        MatchResult result = mock(MatchResult.class);
        CommandRequest request = new CommandRequest(command, args, sender, result, 1);

        assertThat(request.getFirstArg(), is(nullValue()));
    }

    @Test
    public void testGetLastArg() {
        assertThat(request.getLastArg(), is(equalTo("last")));
        int length = request.getArgs().size();
        assertThat(request.getLastArg(), is(equalTo(request.getArg(length - 1))));
    }

    @Test
    public void testGetLastArgNoArgs() {
        List<String> args = new ArrayList<String>();
        CommandSender sender = mock(CommandSender.class);
        Command command = mock(Command.class);
        MatchResult result = mock(MatchResult.class);
        CommandRequest request = new CommandRequest(command, args, sender, result, 1);

        assertThat(request.getLastArg(), is(nullValue()));
    }

    @Test
    public void testIsArgInt() {
        assertTrue(request.isArgInt(1));
        assertFalse(request.isArgInt(2));
        assertFalse(request.isArgInt(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOutOfBoundsIsArgInt() {
        request.isArgInt(-1);
    }

    @Test
    public void testGetInt() {
        assertThat(request.getInt(1), is(2));
        assertThat(request.getInt(2), is(-1));
        assertThat(request.getInt(3), is(-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOutOfBoundsGetInt() {
        request.getInt(1000);
    }

    @Test
    public void testGetWorld() {
        mockStatic(Bukkit.class);

        World world = mock(World.class);
        when(Bukkit.getWorld("world")).thenReturn(world);
        when(Bukkit.getWorld("2")).thenReturn(null);
        when(Bukkit.getWorld("10.23")).thenReturn(null);

        assertThat(request.getWorld(1), is(nullValue()));
        assertThat(request.getWorld(2), is(sameInstance(world)));
        assertThat(request.getWorld(3), is(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetWorldOutOfBounds() {
        request.getWorld(1002);
    }

    @Test
    public void testSenderMessage() {
        request.sendMessage("test message");
        verify(sender, times(1)).sendMessage("test message");
    }

    @Test
    public void testGetArg() {
        assertThat(request.getArg(0), is(equalTo("first")));
        assertThat(request.getArg(1), is(equalTo("2")));
        assertThat(request.getArg(request.getArgs().size() - 1), is(equalTo("last")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArgOutOfBounds() {
        request.getArg(-1);
    }

    @Test
    public void testIsArgPresent() {
        assertTrue(request.isArgPresent(0));
        assertTrue(request.isArgPresent(2));
        assertTrue(request.isArgPresent(4));
        assertFalse(request.isArgPresent(23));
        assertFalse(request.isArgPresent(-1));
        assertFalse(request.isArgPresent(1000));
    }

    @Test
    public void testGetPlayer() {
        mockStatic(Bukkit.class);
        Player mockPlayer = mock(Player.class);
        when(Bukkit.getPlayer("first")).thenReturn(mockPlayer);

        assertThat(request.getPlayer(0), is(sameInstance(mockPlayer)));

        verifyStatic(times(1));
        Bukkit.getPlayer("first");

        assertThat(request.getPlayer(1), is(nullValue()));
        assertThat(request.getPlayer(4), is(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPlayerOutOfBounds() {
        request.getPlayer(-1000);
    }

    @Test
    public void testIsArgNumber() {
        assertTrue(request.isArgNumber(1));
        assertFalse(request.isArgNumber(0));
        assertTrue(request.isArgNumber(3));
        assertFalse(request.isArgNumber(4));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsArgNumberOutOfBounds() {
        request.isArgNumber(12);
    }

    @Test
    public void testGetNumber() {
        assertThat(request.getNumber(1).intValue(), is(2));
        assertThat(request.getNumber(3), is(NumberUtils.createNumber("10.23")));
    }

    @Test(expected = NumberFormatException.class)
    public void testGetNumberInvalid() {
        request.getNumber(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNumberOutOfBounds() {
        request.getNumber(2030);
    }

    @Test
    public void testIsArgBoolean() {
        assertTrue(request.isArgBoolean(5));
        assertTrue(request.isArgBoolean(4));
        assertFalse(request.isArgBoolean(3));
        assertFalse(request.isArgBoolean(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsArgBooleanOutOfBounds() {
        request.isArgBoolean(23);
    }

    @Test
    public void testGetBoolean() {
        assertTrue(request.getBoolean(4));
        assertFalse(request.getBoolean(5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBooleanOutOfBounds() {
        request.getBoolean(23);
    }

    @Test
    public void testGetMatchResult() {
        assertThat(request.getMatcherResult(), is(sameInstance(result)));
    }

    @Test
    public void testGetSender() {
        assertThat(request.getSender(), is(sameInstance(sender)));
    }

    @Test
    public void testGetCommand() {
        assertThat(request.getCommand(), is(sameInstance(command)));
    }

    @Test
    public void testGetSenderType() {
        assertThat(request.getSenderType(), is(SenderType.PLAYER));
    }

    @Before
    public void onStartUp() {
        List<String> args = new ArrayList<String>();
        args.add("first");
        args.add("2");
        args.add("world");
        args.add("10.23");
        args.add("true");
        args.add("no");
        args.add("last");
        sender = mock(Player.class);
        command = mock(Command.class);
        result = mock(MatchResult.class);
        request = new CommandRequest(command, args, sender, result, 1);
    }
}
