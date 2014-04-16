/*
 * SenderTypeTest.java
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

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
public class SenderTypeTest {

    @Test
    public void testGetFromCommandSender() {
        Player player = mock(Player.class);
        ConsoleCommandSender console = mock(ConsoleCommandSender.class);
        BlockCommandSender commandblock = mock(BlockCommandSender.class);
        RemoteConsoleCommandSender remote = mock(RemoteConsoleCommandSender.class);

        assertThat(SenderType.getFromCommandSender(player), is(SenderType.PLAYER));
        assertThat(SenderType.getFromCommandSender(console), is(SenderType.CONSOLE));
        assertThat(SenderType.getFromCommandSender(commandblock), is(SenderType.COMMAND_BLOCK));
        assertThat(SenderType.getFromCommandSender(remote), is(SenderType.REMOTE_CONSOLE));

        CommandSender other = mock(CommandSender.class);
        assertThat(SenderType.getFromCommandSender(other), is(SenderType.OTHER));
    }

}
