/*
 * DefaultCommandRoute.java
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

import com.publicuhc.pluginframework.routing.exception.CommandInvocationException;
import com.publicuhc.pluginframework.routing.proxy.MethodProxy;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

public class DefaultCommandRoute implements CommandRoute
{

    private final MethodProxy proxy;
    private final OptionParser parser;
    private final String commandName;
    private final String[] startsWith;
    private final String permission;
    private final String[] optionPosistions;
    private final OptionSpec helpSpec;
    private final Class<? extends CommandSender>[] allowedSenders;

    public DefaultCommandRoute(String commandName, String permission, Class<? extends CommandSender>[] allowedSenders, MethodProxy proxy, OptionParser parser, String[] optionPosistions, OptionSpec helpSpec)
    {
        this.helpSpec = helpSpec;
        this.optionPosistions = optionPosistions;
        this.allowedSenders = allowedSenders;
        this.proxy = proxy;
        this.parser = parser;
        this.permission = permission.equals(CommandMethod.NO_PERMISSIONS) ? null : permission;

        String[] commandParts = commandName.split(" ");
        this.commandName = commandParts[0];
        this.startsWith = Arrays.copyOfRange(commandParts, 1, commandParts.length);
    }

    @Override
    public OptionParser getOptionDetails()
    {
        return parser;
    }

    @Override
    public MethodProxy getProxy()
    {
        return proxy;
    }

    private void printHelpFor(CommandSender sender)
    {
        //catch the option exceptions and print an error message out with the option syntax
        StringWriter writer = new StringWriter();
        try {
            parser.printHelpOn(writer);
            sender.sendMessage(writer.toString());
        } catch(IOException ioex) {
            //shouldn't run, if it does the world probably exploded
            ioex.printStackTrace();
        }
    }

    private boolean isSenderTypeAllowed(CommandSender sender)
    {
        Class<? extends CommandSender> senderClass = sender.getClass();
        for(Class<? extends CommandSender> senderType : allowedSenders) {
            if(senderType.isAssignableFrom(senderClass)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void run(Command command, CommandSender sender, String[] args) throws CommandInvocationException
    {
        if(!isSenderTypeAllowed(sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot run that command from here!");
            return;
        }

        //check permissions
        String permission = getPermission();
        if(null != permission && !sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to run this command. (" + permission + ")");
            return;
        }

        try {
            OptionSet optionSet = parser.parse(args);
            if(optionSet.has(helpSpec)) {
                printHelpFor(sender);
            } else {
                Object[] parameters = new Object[2 + optionPosistions.length];
                parameters[0] = optionSet;
                parameters[1] = sender;
                for(int i = 0; i < optionPosistions.length; i++) {
                    String option = optionPosistions[i];
                    if(option.equals("[arguments]")) {
                        parameters[i + 2] = optionSet.nonOptionArguments();
                    } else {
                        parameters[i + 2] = optionSet.valueOf(option);
                    }
                }

                proxy.invoke(parameters);
            }
        } catch(OptionException e) {
            printHelpFor(sender);
        } catch(Throwable e) {
            throw new CommandInvocationException("Exception thrown when running the command " + command.getName(), e);
        }
    }

    @Override
    public String getCommandName()
    {
        return commandName;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public String[] getStartsWith()
    {
        return startsWith;
    }

    @Override
    public Class<? extends CommandSender>[] getAllowedSenders()
    {
        return allowedSenders;
    }
}
