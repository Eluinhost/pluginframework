package com.publicuhc.commands.routing;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.publicuhc.commands.annotation.CommandMethod;
import com.publicuhc.commands.annotation.RouteInfo;
import com.publicuhc.commands.annotation.TabCompletion;
import com.publicuhc.commands.exceptions.CommandClassParseException;
import com.publicuhc.commands.proxies.DefaultMethodProxy;
import com.publicuhc.commands.proxies.CommandProxy;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private final HashMap<Command, List<String>> m_noRouteMessages = new HashMap<Command, List<String>>();

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
    protected DefaultRouter(Provider<CommandRequestBuilder> requestProvider, Injector injector, Logger logger){
        m_requestProvider = requestProvider;
        m_injector = injector;
        m_logger = logger;
    }

    @Override
    public List<CommandProxy> getCommandProxy(Command command, String parameters) {
        List<CommandProxy> proxies = new ArrayList<CommandProxy>();
        for(CommandProxy proxy : m_commands){
            if(command.getName().equals(proxy.getBaseCommand().getName())){
                if(proxy.doParamsMatch(parameters)){
                    proxies.add(proxy);
                }
            }
        }
        return proxies;
    }

    @Override
    public List<TabCompleteProxy> getTabCompleteProxy(Command command, String parameters) {
        List<TabCompleteProxy> proxies = new ArrayList<TabCompleteProxy>();
        for(TabCompleteProxy proxy : m_tabCompletes){
            if(command.getName().equals(proxy.getBaseCommand().getName())){
                if(proxy.doParamsMatch(parameters)){
                    proxies.add(proxy);
                }
            }
        }
        return proxies;
    }

    @Override
    public void registerCommands(Class klass) throws CommandClassParseException {
        registerCommands(m_injector.getInstance(klass), false);
    }

    @Override
    public void registerCommands(Object object, boolean inject) throws CommandClassParseException {
        if(inject){
            m_injector.injectMembers(object);
        }
        //the class of our object
        Class klass = object.getClass();

        Method[] methods = klass.getDeclaredMethods();
        for(Method method : methods){
            if(isCommandMethod(method) || isTabComplete(method)){

                //get the method with the details we need
                Method routeInfo;
                try {
                    routeInfo = klass.getMethod(method.getName());
                } catch (NoSuchMethodException e) {
                    m_logger.log(Level.SEVERE,"No method found with the name "+method.getName()+ROUTE_INFO_SUFFIX);
                    throw new CommandClassParseException();
                }

                //check it is valid method
                if(!isRouteInfo(routeInfo)) {
                    m_logger.log(Level.SEVERE, "Route info method " + routeInfo.getName() + " is not valid. (check annotation, parameters and return type are correct");
                    throw new CommandClassParseException();
                }

                //get the details
                MethodRoute methodRoute;
                try {
                    methodRoute = (MethodRoute) routeInfo.invoke(object);
                } catch (Exception e) {
                    e.printStackTrace();
                    m_logger.log(Level.SEVERE, "Error getting route info from the method "+routeInfo.getName());
                    throw new CommandClassParseException();
                }

                //some validation
                PluginCommand command = Bukkit.getPluginCommand(methodRoute.getBaseCommand());
                if(command == null){
                    m_logger.log(Level.SEVERE, "Couldn't find the command "+methodRoute.getBaseCommand()+" for the method "+method.getName());
                    throw new CommandClassParseException();
                }

                //register ourselves
                command.setExecutor(this);
                command.setTabCompleter(this);

                DefaultMethodProxy proxy = null;

                if(isCommandMethod(method)){
                    CommandProxy commandProxy = new CommandProxy();
                    m_commands.add(commandProxy);
                    proxy = commandProxy;
                }
                if(isTabComplete(method)){
                    TabCompleteProxy tabCompleteProxy = new TabCompleteProxy();
                    m_tabCompletes.add(tabCompleteProxy);
                    proxy = tabCompleteProxy;
                }

                assert proxy != null;

                proxy.setPattern(methodRoute.getRoute());
                proxy.setBaseCommand(command);
                proxy.setCommandMethod(method);
                proxy.setInstance(object);
                proxy.setPermission(methodRoute.getPermission());
                proxy.setAllowedSenders(methodRoute.getAllowedTypes());
            }
        }
    }

    /**
     * @param method the method to check
     * @return true if has commandmethod annotation
     */
    private boolean isCommandMethod(Method method){
        return method.getAnnotation(CommandMethod.class) != null;
    }

    /**
     * @param method the method to check
     * @return true if has tabcompletion annotation
     */
    private boolean isTabComplete(Method method){
        return method.getAnnotation(TabCompletion.class) != null;
    }

    /**
     * @param method the method to check
     * @return true if has routeinfo annotation
     */
    private boolean isRouteInfo(Method method){
        return null != method.getAnnotation(RouteInfo.class) && method.getParameterTypes().length <= 0 && MethodRoute.class.isAssignableFrom(method.getReturnType());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        //Put all of the arguments into a string to match
        StringBuilder stringBuilder = new StringBuilder();
        for(String arg : args){
            stringBuilder.append(arg).append(" ");
        }

        //get all the proxies that match the route
        List<CommandProxy> proxies = getCommandProxy(command, stringBuilder.toString());

        //no proxies found that matched the route
        if(proxies.isEmpty()){
            List<String> messages = m_noRouteMessages.get(command);
            //if there isn't any messages send the usage message
            if(messages.isEmpty()){
                return false;
            }
            for(String message : messages){
                sender.sendMessage(message);
            }
            return true;
        }

        //trigger all the proxies
        for(CommandProxy proxy : proxies){
            CommandRequestBuilder builder = m_requestProvider.get();
            CommandRequest request =
                    builder.setCommand(command)
                            .setArguments(args)
                            .setSender(sender)
                            .build();
            try {
                proxy.trigger(request);
            } catch (ProxyTriggerException e) {
                e.getActualException().printStackTrace();
                sender.sendMessage(ChatColor.RED+"Error running command, check console for more information"); //TODO translate with API
            }
        }

        //don't print the error message
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        //Put all of the arguments into a string to match
        StringBuilder stringBuilder = new StringBuilder();
        for(String arg : args){
            stringBuilder.append(arg).append(" ");
        }

        //get all the proxies that match the route
        List<TabCompleteProxy> proxies = getTabCompleteProxy(command, stringBuilder.toString());

        //no proxies found that matched the route
        if(proxies == null){
            return new ArrayList<String>(0);
        }

        //trigger all the proxies and merge them
        List<String> results = new ArrayList<String>();
        for(TabCompleteProxy proxy : proxies){
            CommandRequestBuilder builder = m_requestProvider.get();
            CommandRequest request = builder.setCommand(command)
                    .setArguments(args)
                    .setSender(sender)
                    .build();
            try {
                results.addAll(proxy.trigger(request));
            } catch (ProxyTriggerException e) {
                e.getActualException().printStackTrace();
                sender.sendMessage(ChatColor.RED+"Error with tab completion, check the console for more information"); //TODO translate with API
            }
        }

        return results;
    }
}
