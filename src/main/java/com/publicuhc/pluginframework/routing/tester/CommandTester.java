/*
 * CommandTester.java
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

public interface CommandTester
{
    /**
     * Tests for the given run command. Failure should usually be indicated by sending a message and returning false
     *
     * @param command the command run
     * @param sender the sender that ran it
     * @param args the arguments ran with
     * @return true to continue command, false to quit
     */
    public boolean testCommand(Command command, CommandSender sender, String[] args);
}
