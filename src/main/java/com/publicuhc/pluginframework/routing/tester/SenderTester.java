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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.HashSet;

/**
 * A tester that allows a command if any of the classes provided match the sender. If no senders are defined it will
 * always fail
 */
public class SenderTester extends HashSet<Class<? extends CommandSender>> implements CommandTester
{
    /**
     * Check if all of the supplied classes can be treated as the supplied class
     * @param klass the class to check
     * @return true if no classes in the set or all classes are applicable
     */
    @SuppressWarnings("unchecked")
    public boolean isApplicable(Class klass)
    {
        for(Class<? extends CommandSender> senderType : this) {
            if(!klass.isAssignableFrom(senderType)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks to see if any of the classes in set are superclasses of the provided class
     * @param klass the class to check
     * @return true if any of the classes match, false otherwise or if set is empty
     */
    public boolean isAllowed(Class klass)
    {
        for(Class<? extends CommandSender> senderType : this) {
            if(senderType.isAssignableFrom(klass)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean testCommand(Command command, CommandSender sender, String[] args)
    {
        boolean passed = isAllowed(sender.getClass());

        if(!passed)
            sender.sendMessage(ChatColor.RED + "You cannot run that command from here!");

        return passed;
    }
}
