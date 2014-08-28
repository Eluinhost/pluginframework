/*
 * TesterTest.java
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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class PermissionTesterTest
{
    @Test
    public void test_permission_tester()
    {
        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = new String[0];

        PermissionTester tester = new PermissionTester();

        //test no permissions set pass
        assertThat(tester.testCommand(command, sender, args)).isTrue();

        //test missing permission fail
        tester.add("TEST.PERMISSION.1");
        when(sender.hasPermission("TEST.PERMISSION.1")).thenReturn(false);
        assertThat(tester.testCommand(command, sender, args)).isFalse();

        //test permission pass
        when(sender.hasPermission("TEST.PERMISSION.1")).thenReturn(true);
        assertThat(tester.testCommand(command, sender, args)).isTrue();

        //add another permission
        tester.add("TEST.PERMISSION.2");

        //test matching all fail
        tester.setMatchingAll(true);
        when(sender.hasPermission("TEST.PERMISSION.2")).thenReturn(false);
        assertThat(tester.testCommand(command, sender, args)).isFalse();

        //test not matching all pass
        tester.setMatchingAll(false);
        assertThat(tester.testCommand(command, sender, args)).isTrue();

        //test matching all pass
        tester.setMatchingAll(true);
        when(sender.hasPermission("TEST.PERMISSION.2")).thenReturn(true);
        assertThat(tester.testCommand(command, sender, args)).isTrue();
    }
}
