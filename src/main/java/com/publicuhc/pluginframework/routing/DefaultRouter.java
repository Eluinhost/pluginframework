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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.publicuhc.pluginframework.routing.exception.CommandInvocationException;
import com.publicuhc.pluginframework.routing.exception.CommandParseException;
import com.publicuhc.pluginframework.routing.parser.RoutingMethodParser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginLogger;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

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
     * Stores map of command name -> routes for invocation later on
     */
    protected final HashMap<String, List<CommandRoute>> commands = new HashMap<String, List<CommandRoute>>();

    private final PluginLogger logger;

    @Inject
    protected DefaultRouter(RoutingMethodParser parser, Injector injector, PluginLogger logger)
    {
        this.injector = injector;
        this.parser = parser;
        this.logger = logger;
    }

    @Override
    public Object registerCommands(Class klass) throws CommandParseException {
        return registerCommands(klass, new ArrayList<AbstractModule>());
    }

    @Override
    public Object registerCommands(Class klass, List<AbstractModule> modules) throws CommandParseException
    {
        Injector childInjector = injector.createChildInjector(modules);

        //grab an instance of the class after injection
        Object o = childInjector.getInstance(klass);

        //register it using the instanced version without injection
        registerCommands(o, false);

        //return the created object
        return o;
    }

    @Override
    public void registerCommands(Object object, boolean inject) throws CommandParseException {
        registerCommands(object, inject, new ArrayList<AbstractModule>());
    }

    @Override
    public void registerCommands(Object object, boolean inject, List<AbstractModule> modules) throws CommandParseException
    {
        logger.log(Level.INFO, "Loading commands from class: " + object.getClass().getName());

        //inject if we need to
        if(inject) {
            Injector childInjector = injector.createChildInjector(modules);
            childInjector.injectMembers(object);
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
                List<CommandRoute> routes = commands.get(route.getCommandName());
                if(null == routes) {
                    routes = new ArrayList<CommandRoute>();
                    commands.put(route.getCommandName(), routes);
                }
                routes.add(route);
                logger.log(Level.INFO, "Loading command '" + route.getCommandName() + "' from: " + method.getName());
            }
            //TODO tab complete
        }

        logger.log(Level.INFO, "Loaded all commands from class: " + object.getClass().getName());
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

    /**
     * @param command the command to check for
     * @return list of all the registed routes
     */
    private List<CommandRoute> getRoutesForCommand(Command command)
    {
        String commandName = command.getName();
        List<CommandRoute> routes = commands.get(commandName);
        if(routes == null) {
            routes = new ArrayList<CommandRoute>();
            commands.put(commandName, routes);
        }
        return routes;
    }

    /**
     * Fetches the most applicable route (the routes that matches with the longest subcommand string)
     *
     * @param command the command to check for
     * @param args the argument list to check for subcommands from
     * @return the most applicable route or null if none exists
     */
    private CommandRoute getMostApplicableRoute(Command command, String[] args)
    {
        PriorityQueue<CommandRoute> routes = getApplicableRoutes(command, args, true);
        return routes.peek();
    }

    /**
     * Get all routes that apply for the given command and argument list. Orders by longest subcommand first. e.g.
     * Running 'feature list' would not contain 'feature list subcommand' but would contain 'feature list' and 'feature'
     * in that order
     *
     * @param command the command to check for
     * @param args the argument list to check for subcommands from
     * @param mostApplicable whether to order by the most applicable first (true) or least applicable first (false)
     * @return queue with the first element the most applicable (longest subcommand string)
     */
    private PriorityQueue<CommandRoute> getApplicableRoutes(Command command, String[] args, boolean mostApplicable)
    {
        List<CommandRoute> routes = getRoutesForCommand(command);

        List<String> argsList = Arrays.asList(args);
        PriorityQueue<CommandRoute> applicable = new PriorityQueue<CommandRoute>(Math.max(routes.size(), 1), new SubcommandLengthComparator(mostApplicable));

        for(CommandRoute route : routes) {
            String[] routeStarts = route.getStartsWith();

            //if no starts with it always applies
            if(routeStarts.length == 0) {
                applicable.add(route);
                continue;
            }

            // skip invalid subcommands
            if(routeStarts.length <= argsList.size()) {
                List<String> routeStartsList = Arrays.asList(routeStarts);
                List<String> argsSubList = argsList.subList(0, routeStarts.length);
                if(routeStartsList.equals(argsSubList)) {
                    applicable.add(route);
                }
            }
        }

        return applicable;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        CommandRoute route = getMostApplicableRoute(command, args);

        if(route != null) {
            String[] subcommandArgs = Arrays.copyOfRange(args, route.getStartsWith().length, args.length);

            //run the actual command
            try {
                route.run(command, sender, subcommandArgs);
            } catch(CommandInvocationException e) {
                e.printStackTrace();
            }
            return true;
        }

        //we did't know how to handle this command

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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        //get the item to tab complete
        String partial = args[args.length - 1];
        //TODO check if last arg is empty if they havn't typed anything yet, hope to god it is

        //remove the parial
        String[] withoutPartial = Arrays.copyOfRange(args, 0, args.length - 2);

        //get all the routes that apply to the base command
        PriorityQueue<CommandRoute> allRoutes = getApplicableRoutes(command, withoutPartial, false);

        List<String> options = new ArrayList<String>();

        CommandRoute most = allRoutes.poll();
        if(most != null) {
            //TODO add all the options for the current route
        }

        for(CommandRoute route : allRoutes) {
            //TODO add subcommand minus the current selected subcommand to the list
        }

        return options;
    }
}
