/*
 * CommandRequestTest.java
 *
 * Copyright (c) 2014. Graham Howden <graham_howden1 at yahoo.co.uk>.
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

package com.publicuhc.pluginframework.routing;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.OptionParser;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class CommandRequestTest {

    private String[] args;
    private CommandSender sender;
    private Command command;
    private CommandRequest request;

    @Before
    public void onStartUp() {
        args = new String[] {
                "first",
                "2",
                "world",
                "10.23",
                "true",
                "no",
                "last"
        };
        sender = mock(Player.class);
        command = mock(Command.class);

        OptionParser parser = new OptionParser();
        parser.accepts("a").withRequiredArg();
        request = new CommandRequest(command, sender, parser.parse(args));
    }

    @Test
    public void testRemoveFirstArg() {
        assertThat(request.getArg(0)).isEqualTo("first");

        int length = request.getArgs().size();
        request.removeFirstArg();

        assertThat(request.getArgs().size()).isEqualTo(length -1);
        assertThat(request.getArg(0)).isNotEqualTo("first");
    }

    @Test
    public void testRemoveFirstArgNoArgs() {
        request.setArgs();
        request.removeFirstArg();
    }

    @Test
    public void testGetFirstArg() {
        assertThat(request.getFirstArg()).isEqualTo("first");
    }

    @Test
    public void testGetFirstArgNoArgs() {
        request.setArgs();
        assertThat(request.getFirstArg()).isNull();
    }

    @Test
    public void testGetLastArg() {
        assertThat(request.getLastArg()).isEqualTo("last");
        int length = request.getArgs().size();
        assertThat(request.getLastArg()).isEqualTo(request.getArg(length - 1));
    }

    @Test
    public void testGetLastArgNoArgs() {
        request.setArgs();
        assertThat(request.getLastArg()).isNull();
    }

    @Test
    public void testIsArgInt() {
        assertThat(request.isArgInt(1)).isTrue();
        assertThat(request.isArgInt(2)).isFalse();
        assertThat(request.isArgInt(3)).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOutOfBoundsIsArgInt() {
        request.isArgInt(-1);
    }

    @Test
    public void testGetInt() {
        assertThat(request.getInt(1)).isEqualTo(2);
        assertThat(request.getInt(2)).isEqualTo(-1);
        assertThat(request.getInt(3)).isEqualTo(-1);
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

        assertThat(request.getWorld(1)).isNull();
        assertThat(request.getWorld(2)).isSameAs(world);
        assertThat(request.getWorld(3)).isNull();
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
        assertThat(request.getArg(0)).isEqualTo("first");
        assertThat(request.getArg(1)).isEqualTo("2");
        assertThat(request.getArg(request.getArgs().size() - 1)).isEqualTo("last");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArgOutOfBounds() {
        request.getArg(-1);
    }

    @Test
    public void testIsArgPresent() {
        assertThat(request.isArgPresent(0)).isTrue();
        assertThat(request.isArgPresent(2)).isTrue();
        assertThat(request.isArgPresent(4)).isTrue();
        assertThat(request.isArgPresent(23)).isFalse();
        assertThat(request.isArgPresent(-1)).isFalse();
        assertThat(request.isArgPresent(1000)).isFalse();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetPlayer() {
        mockStatic(Bukkit.class);
        Player mockPlayer = mock(Player.class);
        when(Bukkit.getPlayer("first")).thenReturn(mockPlayer);

        assertThat(request.getPlayer(0)).isSameAs(mockPlayer);

        verifyStatic(times(1));
        Bukkit.getPlayer("first");

        assertThat(request.getPlayer(1)).isNull();
        assertThat(request.getPlayer(4)).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPlayerOutOfBounds() {
        request.getPlayer(-1000);
    }

    @Test
    public void testIsArgNumber() {
        assertThat(request.isArgNumber(1)).isTrue();
        assertThat(request.isArgNumber(0)).isFalse();
        assertThat(request.isArgNumber(3)).isTrue();
        assertThat(request.isArgNumber(4)).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsArgNumberOutOfBounds() {
        request.isArgNumber(12);
    }

    @Test
    public void testGetNumber() {
        assertThat(request.getNumber(1).intValue()).isEqualTo(2);
        assertThat(request.getNumber(3)).isEqualTo(NumberUtils.createNumber("10.23"));
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
        assertThat(request.isArgBoolean(5)).isTrue();
        assertThat(request.isArgBoolean(4)).isTrue();
        assertThat(request.isArgBoolean(3)).isFalse();
        assertThat(request.isArgBoolean(2)).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsArgBooleanOutOfBounds() {
        request.isArgBoolean(23);
    }

    @Test
    public void testGetBoolean() {
        assertThat(request.getBoolean(4)).isTrue();
        assertThat(request.getBoolean(5)).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBooleanOutOfBounds() {
        request.getBoolean(23);
    }

    @Test
    public void testGetSender() {
        assertThat(request.getSender()).isSameAs(sender);
    }

    @Test
    public void testGetCommand() {
        assertThat(request.getCommand()).isSameAs(command);
    }

    @Test
    public void testGetSenderType() {
        assertThat(request.getSenderType()).isEqualTo(SenderType.PLAYER);
    }
}
