/*
 * SenderTester.java
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
