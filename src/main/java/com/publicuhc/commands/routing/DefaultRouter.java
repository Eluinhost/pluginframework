/*
 * DefaultRouter.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
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

package com.publicuhc.commands.routing;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.publicuhc.commands.annotation.CommandMethod;
import com.publicuhc.commands.annotation.RouteInfo;
import com.publicuhc.commands.annotation.TabCompletion;
import com.publicuhc.commands.exceptions.*;
import com.publicuhc.commands.proxies.CommandProxy;
import com.publicuhc.commands.proxies.DefaultMethodProxy;
import com.publicuhc.commands.proxies.ProxyTriggerException;
import com.publicuhc.commands.proxies.TabCompleteProxy;
import com.publicuhc.commands.requests.CommandRequest;
import com.publicuhc.commands.requests.CommandRequestBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;

public class DefaultRouter implements Router {

    /**
     * Stores all the command proxies
     */
    private final ArrayList<CommandProxy> m_commands = new ArrayList<CommandProxy>();

    /**
     * Stores all the tab complete proxies
     */
    private final ArrayList<TabCompleteProxy> m_tabCompletes = new ArrayList<TabCompleteProxy>();

    /**
     * Stores the message to send a player if a route wasn't found for the given command and parameters
     */
    private final HashMap<Command, List<String>> m_noRouteMessages = new HashMap<Command, List<String>>(); //TODO be able to set the defaults

    /**
     * Used to build requests
     */
    private final Provider<CommandRequestBuilder> m_requestProvider;

    /**
     * Used to inject all parameters needed to the command classes when added
     */
    private final Injector m_injector;

    private final Logger m_logger;

    private static final String ROUTE_INFO_SUFFIX = "Details";

    @Inject
    protected DefaultRouter(Provider<CommandRequestBuilder> requestProvider, Injector injector, Logger logger) {
        m_requestProvider = requestProvider;
        m_injector = injector;
        m_logger = logger;
    }

    @Override
    public Map<CommandProxy, MatchResult> getCommandProxy(Command command, String parameters) {
        Map<CommandProxy, MatchResult> proxies = new HashMap<CommandProxy, MatchResult>();
        for (CommandProxy proxy : m_commands) {
            if (command.getName().equals(proxy.getBaseCommand().getName())) {
                MatchResult result = proxy.paramsMatch(parameters);
                if (result != null) {
                    proxies.put(proxy, result);
                }
            }
        }
        return proxies;
    }

    @Override
    public Map<TabCompleteProxy, MatchResult> getTabCompleteProxy(Command command, String parameters) {
        Map<TabCompleteProxy, MatchResult> proxies = new HashMap<TabCompleteProxy, MatchResult>();
        for (TabCompleteProxy proxy : m_tabCompletes) {
            if (command.getName().equals(proxy.getBaseCommand().getName())) {
                MatchResult result = proxy.paramsMatch(parameters);
                if (result != null) {
                    proxies.put(proxy, result);
                }
            }
        }
        return proxies;
    }

    @Override
    public Object registerCommands(Class klass) throws CommandClassParseException {
        Object o = m_injector.getInstance(klass);
        registerCommands(o, false);
        return o;
    }

    protected void checkParameters(Method method) throws InvalidMethodParametersException {
        if (method.getParameterTypes().length != 1 || !CommandRequest.class.isAssignableFrom(method.getParameterTypes()[0])) {
            m_logger.log(Level.SEVERE, "Method " + method.getName() + " has incorrect parameters");
            throw new InvalidMethodParametersException();
        }
    }

    @Override
    public void registerCommands(Object object, boolean inject) throws CommandClassParseException {
        if (inject) {
            m_injector.injectMembers(object);
        }
        //the class of our object
        Class klass = object.getClass();

        Method[] methods = klass.getDeclaredMethods();
        for (Method method : methods) {
            boolean isCommandMethod = isCommandMethod(method);
            boolean isTabComplete = isTabComplete(method);

            if (isCommandMethod || isTabComplete) {

                //check the method parameters are correct
                checkParameters(method);

                if (isTabComplete) {
                    checkTabCompleteReturn(method);
                }

                //get the method with the details we need
                Method routeInfo;
                try {
                    routeInfo = klass.getMethod(method.getName() + ROUTE_INFO_SUFFIX);
                } catch (NoSuchMethodException e) {
                    m_logger.log(Level.SEVERE, "No method found with the name " + method.getName() + ROUTE_INFO_SUFFIX);
                    throw new DetailsMethodNotFoundException();
                }

                //throws exceptions if not valid
                checkRouteInfo(routeInfo);

                //get the details
                MethodRoute methodRoute;
                try {
                    methodRoute = (MethodRoute) routeInfo.invoke(object);
                } catch (Exception e) {
                    e.printStackTrace();
                    m_logger.log(Level.SEVERE, "Error getting route info from the method " + routeInfo.getName());
                    throw new CommandClassParseException();
                }

                //some validation
                PluginCommand command = Bukkit.getPluginCommand(methodRoute.getBaseCommand());
                if (command == null) {
                    m_logger.log(Level.SEVERE, "Couldn't find the command " + methodRoute.getBaseCommand() + " for the method " + method.getName());
                    throw new BaseCommandNotFoundException();
                }

                //register ourselves
                command.setExecutor(this);
                command.setTabCompleter(this);

                DefaultMethodProxy proxy = null;

                if (isCommandMethod) {
                    CommandProxy commandProxy = new CommandProxy();
                    m_commands.add(commandProxy);
                    proxy = commandProxy;
                }
                if (isTabComplete) {
                    TabCompleteProxy tabCompleteProxy = new TabCompleteProxy();
                    m_tabCompletes.add(tabCompleteProxy);
                    proxy = tabCompleteProxy;
                }

                proxy.setPattern(methodRoute.getRoute());
                proxy.setBaseCommand(command);
                proxy.setCommandMethod(method);
                proxy.setInstance(object);
                proxy.setPermission(methodRoute.getPermission());
                proxy.setAllowedSenders(methodRoute.getAllowedTypes());
            }
        }
    }

    protected void checkTabCompleteReturn(Method method) throws InvalidReturnTypeException {
        //only allow list returns
        if (!List.class.isAssignableFrom(method.getReturnType())) {
            throw new InvalidReturnTypeException();
        }

        Type type = method.getGenericReturnType();

        //make sure its a string parameter
        ParameterizedType ptype = (ParameterizedType) type;
        Type[] types = ptype.getActualTypeArguments();
        if (types.length != 1 || !String.class.isAssignableFrom((Class) types[0])) {
            throw new InvalidReturnTypeException();
        }
    }

    /**
     * @param method the method to check
     * @return true if has commandmethod annotation
     */
    protected boolean isCommandMethod(Method method) {
        return method.getAnnotation(CommandMethod.class) != null;
    }

    /**
     * @param method the method to check
     * @return true if has tabcompletion annotation
     */
    protected boolean isTabComplete(Method method) {
        return method.getAnnotation(TabCompletion.class) != null;
    }

    /**
     * Check the method to see if it is a valid routeinfo method
     *
     * @param method the method to check
     * @throws com.publicuhc.commands.exceptions.AnnotationMissingException if the method doesn't have the @RouteInfo annotation
     * @throws com.publicuhc.commands.exceptions.InvalidReturnTypeException if the method doesn't return a MethodRoute
     */
    protected void checkRouteInfo(Method method) throws CommandClassParseException {
        if (null == method.getAnnotation(RouteInfo.class)) {
            m_logger.log(Level.SEVERE, "Route info method " + method.getName() + " does not have the @RouteInfo annotation");
            throw new AnnotationMissingException();
        }
        if (!MethodRoute.class.isAssignableFrom(method.getReturnType())) {
            m_logger.log(Level.SEVERE, "Route info method " + method.getName() + " does not have the correct return type");
            throw new InvalidReturnTypeException();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        //Put all of the arguments into a string to match
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> argI = Arrays.asList(args).iterator();
        while (argI.hasNext()) {
            String arg = argI.next();
            stringBuilder.append(arg);
            if (argI.hasNext()) {
                stringBuilder.append(" ");
            }
        }

        //get all the proxies that match the route
        Map<CommandProxy, MatchResult> proxies = getCommandProxy(command, stringBuilder.toString());

        //no proxies found that matched the route
        if (proxies.isEmpty()) {
            List<String> messages = m_noRouteMessages.get(command);
            //if there isn't any messages send the usage message
            if (messages == null || messages.isEmpty()) {
                return false;
            }
            for (String message : messages) {
                sender.sendMessage(message);
            }
            return true;
        }

        //trigger all the proxies
        for (CommandProxy proxy : proxies.keySet()) {
            CommandRequestBuilder builder = m_requestProvider.get();
            CommandRequest request =
                    builder.setCommand(command)
                            .setArguments(args)
                            .setSender(sender)
                            .setMatchResult(proxies.get(proxy))
                            .build();
            try {
                proxy.trigger(request);
            } catch (ProxyTriggerException e) {
                e.getActualException().printStackTrace();
                sender.sendMessage(ChatColor.RED + "Error running command, check console for more information"); //TODO translate with API
            }
        }

        //don't print the error message
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        //Put all of the arguments into a string to match
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> argI = Arrays.asList(args).iterator();
        while (argI.hasNext()) {
            String arg = argI.next();
            stringBuilder.append(arg);
            if (argI.hasNext()) {
                stringBuilder.append(" ");
            }
        }

        //get all the proxies that match the route
        Map<TabCompleteProxy, MatchResult> proxies = getTabCompleteProxy(command, stringBuilder.toString());

        //no proxies found that matched the route
        if (proxies == null) {
            return new ArrayList<String>(0);
        }

        //trigger all the proxies and merge them
        List<String> results = new ArrayList<String>();
        for (TabCompleteProxy proxy : proxies.keySet()) {
            CommandRequestBuilder builder = m_requestProvider.get();
            CommandRequest request = builder.setCommand(command)
                    .setArguments(args)
                    .setSender(sender)
                    .setMatchResult(proxies.get(proxy))
                    .build();
            try {
                results.addAll(proxy.trigger(request));
            } catch (ProxyTriggerException e) {
                e.getActualException().printStackTrace();
                sender.sendMessage(ChatColor.RED + "Error with tab completion, check the console for more information"); //TODO translate with API
            }
        }

        return results;
    }
}
