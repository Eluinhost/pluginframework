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

package com.publicuhc.pluginframework.commands.routing;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.annotation.TabCompletion;
import com.publicuhc.pluginframework.commands.exceptions.*;
import com.publicuhc.pluginframework.commands.proxies.CommandProxy;
import com.publicuhc.pluginframework.commands.proxies.DefaultMethodProxy;
import com.publicuhc.pluginframework.commands.proxies.ProxyTriggerException;
import com.publicuhc.pluginframework.commands.proxies.TabCompleteProxy;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.requests.CommandRequestBuilder;
import com.publicuhc.pluginframework.translate.Translate;
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
    private final HashMap<String, List<String>> m_noRouteMessages = new HashMap<String, List<String>>();

    /**
     * Used to build requests
     */
    private final Provider<CommandRequestBuilder> m_requestProvider;

    /**
     * Used to translate messages
     */
    private final Translate m_translator;

    /**
     * Used to inject all parameters needed to the command classes when added
     */
    private final Injector m_injector;

    private final Logger m_logger;

    private static final String ROUTE_INFO_SUFFIX = "Details";

    @Inject
    protected DefaultRouter(Provider<CommandRequestBuilder> requestProvider, Injector injector, Logger logger, Translate translator) {
        m_requestProvider = requestProvider;
        m_injector = injector;
        m_logger = logger;
        m_translator = translator;
    }

    @Override
    public List<CommandProxy> getCommandProxy(CommandSender sender, Command command, String parameters) {
        List<CommandProxy> proxies = new ArrayList<CommandProxy>();
        for (CommandProxy proxy : m_commands) {
            Route route = proxy.getRoute();
            if( route.matches(sender, command, parameters )) {
                proxies.add(proxy);
            }
        }
        return proxies;
    }

    @Override
    public List<TabCompleteProxy> getTabCompleteProxy(CommandSender sender, Command command, String parameters) {
        List<TabCompleteProxy> proxies = new ArrayList<TabCompleteProxy>();
        for (TabCompleteProxy proxy : m_tabCompletes) {
            Route route = proxy.getRoute();
            if( route.matches(sender, command, parameters) ){
                proxies.add(proxy);
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
                Route methodRoute;
                try {
                    methodRoute = (Route) routeInfo.invoke(object);
                } catch (Exception e) {
                    e.printStackTrace();
                    m_logger.log(Level.SEVERE, "Error getting route info from the method " + routeInfo.getName());
                    throw new CommandClassParseException();
                }

                checkRouteChainValid(methodRoute);

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

                proxy.setRoute(methodRoute);
                proxy.setCommandMethod(method);
                proxy.setInstance(object);
            }
        }
    }

    @Override
    public void setDefaultMessageForCommand(String commandName, List<String> message) {
        m_noRouteMessages.put(commandName, message);
    }

    @Override
    public void setDefaultMessageForCommand(String commandName, String message) {
        ArrayList<String> mes = new ArrayList<String>(1);
        mes.add(message);
        setDefaultMessageForCommand(commandName, mes);
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
     * @throws com.publicuhc.pluginframework.commands.exceptions.AnnotationMissingException if the method doesn't have the @RouteInfo annotation
     * @throws com.publicuhc.pluginframework.commands.exceptions.InvalidReturnTypeException if the method doesn't return a MethodRoute
     */
    protected void checkRouteInfo(Method method) throws CommandClassParseException {
        if (null == method.getAnnotation(RouteInfo.class)) {
            m_logger.log(Level.SEVERE, "Route info method " + method.getName() + " does not have the @RouteInfo annotation");
            throw new AnnotationMissingException();
        }
        if (!Route.class.isAssignableFrom(method.getReturnType())) {
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
        List<CommandProxy> proxies = getCommandProxy(sender, command, stringBuilder.toString());

        //no proxies found that matched the route
        if (proxies.isEmpty()) {
            List<String> messages = m_noRouteMessages.get(command.getName());
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
        for (CommandProxy proxy : proxies) {
            Route route = proxy.getRoute();
            if(route.getMaxMatches() != 0 && route.getMaxMatches() < proxies.size()) {
                continue;
            }
            CommandRequestBuilder builder = m_requestProvider.get();
            CommandRequest request =
                    builder.setCommand(command)
                            .setArguments(args)
                            .setSender(sender)
                            .setCount(proxies.size())
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
        List<TabCompleteProxy> proxies = getTabCompleteProxy(sender, command, stringBuilder.toString());

        //no proxies found that matched the route
        if (proxies == null) {
            return new ArrayList<String>(0);
        }

        //trigger all the proxies and merge them
        List<String> results = new ArrayList<String>();
        for (TabCompleteProxy proxy : proxies) {
            CommandRequestBuilder builder = m_requestProvider.get();
            CommandRequest request = builder.setCommand(command)
                    .setArguments(args)
                    .setSender(sender)
                    .setCount(proxies.size())
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

    protected void checkRouteChainValid(Route route) throws BaseCommandNotFoundException {
        checkRouteRestrictionValid(route);

        Route newRoute = route.getNextChain();
        if(newRoute != null) {
            checkRouteChainValid(newRoute);
        }
    }

    protected void checkRouteRestrictionValid(Route route) throws BaseCommandNotFoundException {
        if(route instanceof CommandRestrictedRoute) {
            String commandName = ((CommandRestrictedRoute) route).getCommand();
            PluginCommand command = Bukkit.getPluginCommand(commandName);
            if (command == null) {
                throw new BaseCommandNotFoundException(commandName);
            }
            //register ourselves
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
    }
}
