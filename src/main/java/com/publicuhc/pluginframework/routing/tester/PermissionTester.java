/*
 * PermissionTester.java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.publicuhc.pluginframework.routing.tester;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.HashSet;

/**
 * A tester that allows the command based on the permissions of the sender. NOTE: If no permissions are defined, it will always
 * pass.
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
