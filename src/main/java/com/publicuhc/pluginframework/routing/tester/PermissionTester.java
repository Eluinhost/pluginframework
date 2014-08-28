/*
 * DefaultPermissionTester.java
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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.HashSet;

/**
 * A tester that allows the command based on the permissions of the sender
 */
public class PermissionTester extends HashSet<String> implements CommandTester
{
    boolean matchingAll = true;

    public boolean isMatchingAll()
    {
        return matchingAll;
    }

    public void setMatchingAll(boolean matching)
    {
        matchingAll = matching;
    }

    @Override
    public boolean testCommand(Command command, CommandSender sender, String[] args)
    {
        //check permissions
        for(String permission : this) {
            boolean matching = sender.hasPermission(permission);
            if(matchingAll && !matching) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to run this command. (" + permission + ")");
                return false;
            }
            if(!matchingAll && matching) {
                break;
            }
        }
        return true;
    }
}
