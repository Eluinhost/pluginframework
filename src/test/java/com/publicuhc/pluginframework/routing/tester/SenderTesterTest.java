/*
 * SenderTesterTest.java
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

package com.publicuhc.pluginframework.routing.tester;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
public class SenderTesterTest
{
    @Test
    public void test_sender_tester()
    {
        Command command = mock(Command.class);
        CommandSender commandSender = mock(CommandSender.class);
        Player player = mock(Player.class);
        BlockCommandSender bSender = mock(BlockCommandSender.class);
        String[] args = new String[0];

        SenderTester tester = new SenderTester();

        //test no senders specified - fail
        assertThat(tester.testCommand(command, commandSender, args)).isFalse();

        //test with same class
        tester.add(CommandSender.class);
        assertThat(tester.testCommand(command, commandSender, args)).isTrue();

        tester.clear();

        tester.add(Player.class);
        assertThat(tester.testCommand(command, player, args)).isTrue();

        //test with invalid class
        assertThat(tester.testCommand(command, bSender, args)).isFalse();

        //test mutliclass
        tester.add(BlockCommandSender.class);
        assertThat(tester.testCommand(command, bSender, args)).isTrue();
        assertThat(tester.testCommand(command, player, args)).isTrue();

        //test inheritance
        tester.clear();
        tester.add(CommandSender.class);
        assertThat(tester.testCommand(command, player, args)).isTrue();
        assertThat(tester.testCommand(command, bSender, args)).isTrue();
    }
}
