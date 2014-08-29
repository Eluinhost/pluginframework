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
import com.publicuhc.pluginframework.routing.tester.CommandTester;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultCommandRoute implements CommandRoute
{

    private final MethodProxy proxy;
    private final OptionParser parser;
    private final String commandName;
    private final String[] startsWith;
    private final String[] optionPosistions;
    private final OptionSpec helpSpec;
    private final List<CommandTester> restrictions;

    public DefaultCommandRoute(String commandName, MethodProxy proxy, OptionParser parser, String[] optionPosistions, OptionSpec helpSpec, List<CommandTester> restrictions)
    {
        this.helpSpec = helpSpec;
        this.optionPosistions = optionPosistions;
        this.proxy = proxy;
        this.parser = parser;
        this.restrictions = restrictions;

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

    private String[] fixQuoted(String[] args)
    {
        List<String> fixed = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        boolean insideQuotes = false;
        for (String arg : args) {
            if (arg.startsWith("\"")) {
                insideQuotes = true;
            }
            if (insideQuotes) {
                //add turn '" arg' into ' arg'
                builder.append(" ").append(arg.substring(1));
            }
            if (arg.endsWith("\"")) {
                insideQuotes = false;
                builder.append(" ").append(arg.substring(0, arg.length() - 1));
                //remove the extra " " at the start of the arg
                fixed.add(builder.toString().substring(1));
                builder = new StringBuilder();
            }
        }

        //check and add any trailing data that wasn't closed
        String trailing = builder.toString();
        if(trailing.length() > 0) {
            //remove the extra " " at the start of the arg
            fixed.add(builder.toString().substring(1));
        }
        return fixed.toArray(new String[fixed.size()]);
    }

    @Override
    public void run(Command command, CommandSender sender, String[] args) throws CommandInvocationException
    {
        //check the quoted arguments
        args = fixQuoted(args);

        //run all of the restrictions
        for(CommandTester tester : restrictions) {
            if(!tester.testCommand(command, sender, args)) {
                return;
            }
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
    public String[] getStartsWith()
    {
        return startsWith;
    }

    @Override
    public List<CommandTester> getTesters()
    {
        return restrictions;
    }
}
