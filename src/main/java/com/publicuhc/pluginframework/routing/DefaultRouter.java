/*
 * DefaultRouter.java
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

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.publicuhc.pluginframework.routing.exception.CommandInvocationException;
import com.publicuhc.pluginframework.routing.exception.CommandParseException;
import com.publicuhc.pluginframework.routing.parser.RoutingMethodParser;
import joptsimple.OptionException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DefaultRouter implements Router
{

    /**
     * Stores the message to send a player if a route wasn't found for the given command and parameters
     */
    protected final HashMap<String, List<String>> noRouteMessages = new HashMap<String, List<String>>();

    /**
     * Used to inject all parameters needed to the command classes when added
     */
    private final Injector injector;

    /**
     * Used to parse the annotations into usable objects
     */
    private final RoutingMethodParser parser;

    /**
     * Stores map of command name -> route for invocation later on
     */
    protected final HashMap<String, CommandRoute> commands = new HashMap<String, CommandRoute>();

    @Inject
    protected DefaultRouter(RoutingMethodParser parser, Injector injector)
    {
        this.injector = injector;
        this.parser = parser;
    }

    @Override
    public Object registerCommands(Class klass) throws CommandParseException
    {
        //grab an instance of the class after injection
        Object o = injector.getInstance(klass);

        //register it using the instanced version without injection
        registerCommands(o, false);

        //return the created object
        return o;
    }

    @Override
    public void registerCommands(Object object, boolean inject) throws CommandParseException
    {
        //inject if we need to
        if(inject) {
            injector.injectMembers(object);
        }

        //the class of our object
        Class klass = object.getClass();

        //get all of the classes methods
        Method[] methods = klass.getDeclaredMethods();
        for(Method method : methods) {
            //if it's a command method
            if(parser.hasCommandMethodAnnotation(method)) {

                //attempt to parse the route
                CommandRoute route = parser.parseCommandMethodAnnotation(method, object);

                PluginCommand command = Bukkit.getPluginCommand(route.getCommandName());
                //if the command required is 'ungettable' throw an error
                if(null == command)
                    throw new CommandParseException("Cannot register the command with name " + route.getCommandName());

                //set ourselves as the executor for the command
                command.setExecutor(this);
                command.setTabCompleter(this);

                //add to command map
                commands.put(route.getCommandName(), route);
            }
            //TODO tab complete
        }
    }

    @Override
    public void setDefaultMessageForCommand(String commandName, List<String> message)
    {
        noRouteMessages.put(commandName, message);
    }

    @Override
    public void setDefaultMessageForCommand(String commandName, String message)
    {
        //create a list of size 1 with the message
        ArrayList<String> mes = new ArrayList<String>(1);
        mes.add(message);

        //run the regular command
        setDefaultMessageForCommand(commandName, mes);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        CommandRoute route = commands.get(command.getName());

        //if we don't know how to handle this command
        if(null == route) {
            //get the list of messages set as the defaults for the command
            List<String> messages = noRouteMessages.get(command.getName());

            //if none are set use the one set in the plugin.yml via bukkit
            if(null == messages) {
                return false;
            }

            //send all of the messages and return true to bukkit
            for(String message : messages) {
                sender.sendMessage(message);
            }
            return true;
        }

        //check permissions
        String permission = route.getPermission();
        if(null != permission && !sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to run that command. (" + permission + ")");
            return true;
        }

        //run the actual command
        try {
            route.run(command, sender, args);
        } catch(OptionException ex) {
            //catch the option exceptions and print an error message out with the option syntax
            StringWriter writer = new StringWriter();
            try {
                route.getOptionDetails().getParser().printHelpOn(writer);
                sender.sendMessage(writer.toString());
                return true;
            } catch(IOException ioex) {
                ex.printStackTrace();
                return false;
            }
        } catch(CommandInvocationException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        //TODO tab complete
        return new ArrayList<String>();
    }
}
