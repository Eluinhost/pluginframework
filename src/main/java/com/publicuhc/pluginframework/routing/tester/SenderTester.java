/*
 * SenderTester.java
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

import java.util.HashSet;

/**
 * A tester that allows a command if any of the classes provided match the sender. If no senders are defined it will
 * always fail
 */
public class SenderTester extends HashSet<Class<? extends CommandSender>> implements CommandTester
{
    @Override
    public boolean testCommand(Command command, CommandSender sender, String[] args)
    {
        Class<? extends CommandSender> senderClass = sender.getClass();
        for(Class<? extends CommandSender> senderType : this) {
            if(senderType.isAssignableFrom(senderClass)) {
                return true;
            }
        }
        return false;
    }
}
