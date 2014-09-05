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

import com.google.common.base.*;
import com.google.common.collect.*;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.publicuhc.pluginframework.routing.exception.CommandInvocationException;
import com.publicuhc.pluginframework.routing.exception.CommandParseException;
import com.publicuhc.pluginframework.routing.functions.ApplicableRoutePredicate;
import com.publicuhc.pluginframework.routing.functions.ExactRouteMatchPredicate;
import com.publicuhc.pluginframework.routing.functions.SubroutePredicate;
import com.publicuhc.pluginframework.routing.parser.RoutingMethodParser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.util.StringUtil;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

public class DefaultRouter implements Router
{

    /**
     * Stores the message to send a player if a route wasn't found for the given command and parameters
     */
    protected final Multimap<String, String> noRouteMessages = ArrayListMultimap.create();

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
    protected final Multimap<String, CommandRoute> commands = HashMultimap.create();

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
                commands.put(route.getCommandName(), route);

                logger.log(Level.INFO, "Loading command '" + route.getCommandName() + "' from: " + method.getName());
            }
        }

        logger.log(Level.INFO, "Loaded all commands from class: " + object.getClass().getName());
    }

    @Override
    public void setDefaultMessageForCommand(String commandName, List<String> message)
    {
        noRouteMessages.replaceValues(commandName, message);
    }

    @Override
    public void setDefaultMessageForCommand(String commandName, String message)
    {
        //run the regular method
        setDefaultMessageForCommand(commandName, Lists.newArrayList(message));
    }

    /**
     * Fetches the most applicable route (the routes that matches with the longest subcommand string)
     *
     * @param command the command to check for
     * @param args the argument list to check for subcommands from
     * @return the most applicable route
     */
    private Optional<CommandRoute> getMostApplicableRoute(Command command, String[] args)
    {
        List<CommandRoute> routes = getApplicableRoutes(command, args);

        if(routes.size() == 0) {
            return Optional.absent();
        }

        Collections.sort(routes, new SubcommandLengthComparator(true));

        return Optional.of(routes.get(0));
    }

    /**
     * Get a list of all of the routes for the command filtered to all routes that apply to the given args
     *
     * @param command the command to check the route of
     * @param args the argument list to check for subcommands from
     * @return collection of routes that match
     */
    private List<CommandRoute> getApplicableRoutes(Command command, String[] args)
    {
        Collection<CommandRoute> allRoutes = commands.get(command.getName());

        return Lists.newArrayList(Collections2.filter(allRoutes, new ApplicableRoutePredicate(args, false)));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Optional<CommandRoute> routeOptional = getMostApplicableRoute(command, args);

        if(routeOptional.isPresent()) {
            CommandRoute route = routeOptional.get();

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
        Collection<String> messages = noRouteMessages.get(command.getName());

        //if none are set use the one set in the plugin.yml via bukkit
        if(messages.isEmpty()) {
            return false;
        }

        //send all of the messages and return true to bukkit
        for(String message : messages) {
            sender.sendMessage(message);
        }
        return true;
    }

    /**
     * On tab complete. The final arg is the one we want to match against, can be empty
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        Preconditions.checkArgument(args.length > 0);
        List<String> tabCompleteList = Lists.newArrayList();

        List<String> route = Lists.newArrayList(args);

        //get the item to tab complete and remove it from the list
        String partial = route.get(route.size() - 1);
        route = route.subList(0, route.size() - 1);

        //check if already have any flags
        int index = Iterables.indexOf(route, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return input.startsWith("-");
            }
        });

        //if there are any options remove everything past the first one
        if(index > -1) {
            route = route.subList(0, index);
        }

        //get all of the routes for the command
        Collection<CommandRoute> allRoutes = commands.get(command.getName());

        //all of the subroutes
        Collection<CommandRoute> subroutes = Collections2.filter(allRoutes, new SubroutePredicate(route));

        //exact matches
        Collection<CommandRoute> exacts = Collections2.filter(allRoutes, new ExactRouteMatchPredicate(route));

        for(CommandRoute r : exacts) {
            for (String key : r.getOptionDetails().recognizedOptions().keySet()) {
                if (!key.equals("[arguments]")) {
                    tabCompleteList.add("-" + key);
                }
            }
        }

        for(CommandRoute r : subroutes) {
            //this should pass as the predicate makes sure there is a next arg
            String nextInSubroute = r.getStartsWith()[route.size()];
            tabCompleteList.add(nextInSubroute);
        }

        List<String> actualComplete = Lists.newArrayList();
        StringUtil.copyPartialMatches(partial, tabCompleteList, actualComplete);
        return actualComplete;
    }
}
