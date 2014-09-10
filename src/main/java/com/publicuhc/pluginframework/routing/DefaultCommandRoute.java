/*
 * DefaultCommandRoute.java
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

package com.publicuhc.pluginframework.routing;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.publicuhc.pluginframework.routing.exception.CommandInvocationException;
import com.publicuhc.pluginframework.routing.proxy.MethodProxy;
import com.publicuhc.pluginframework.routing.tester.CommandTester;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

    protected String[] fixQuoted(String[] args)
    {
        List<String> fixed = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        boolean insideQuotes = false;
        for (String arg : args) {
            if(!insideQuotes && arg.startsWith("\"")) {
                insideQuotes = true;
                arg = arg.substring(1);
            }
            if (insideQuotes && arg.endsWith("\"")) {
                insideQuotes = false;
                arg = arg.substring(0, arg.length() - 1);
                builder.append(" ").append(arg);
                //remove the extra " " at the start of the arg
                arg = builder.toString().substring(1);
                builder = new StringBuilder();
            }
            if (insideQuotes) {
                builder.append(" ").append(arg);
            } else {
                fixed.add(arg);
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
            Collection<String> helpOptions = Collections2.<String, String>transform(helpSpec.options(), new Function<String, String>()
            {
                @Override
                public String apply(@Nullable String option)
                {
                    return "-" + option;
                }
            });

            FancyMessage message =
                    new FancyMessage("Failed to run command: ")
                            .color(ChatColor.RED)
                    .then(e.getCause() == null ? e.getMessage() : e.getCause().getMessage())
                            .color(ChatColor.BLUE)
                    .then(" To check the syntax run: ")
                            .color(ChatColor.RED)
                    .then("/" + command.getName() + " " + Joiner.on(" OR ").join(helpOptions))
                        .style(ChatColor.UNDERLINE)
                        .suggest("/"+command.getName() + " -" + helpOptions.iterator().next());

            message.send(sender);
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
