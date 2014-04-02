package com.publicuhc.commands.routing;

import com.google.common.collect.MutableClassToInstanceMap;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.publicuhc.commands.CommandProxy;
import com.publicuhc.commands.requests.CommandRequest;
import com.publicuhc.commands.requests.CommandRequestBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DefaultRouter implements Router {

    /**
     * Stores all the command proxies
     */
    private final ArrayList<CommandProxy> m_commands = new ArrayList<CommandProxy>();

    /**
     * Store the instances of the classes to use
     */
    private final MutableClassToInstanceMap m_instances = MutableClassToInstanceMap.create();

    /**
     * Used to build requests
     */
    private final Provider<CommandRequestBuilder> m_requestProvider;

    @Inject
    public DefaultRouter(Provider<CommandRequestBuilder> requestProvider){
        m_requestProvider = requestProvider;
    }


    @Override
    @Nullable
    public CommandProxy getForBaseCommand(String command) {
        for(CommandProxy proxy : m_commands){
            if(command.equals(proxy.getBaseCommand().getName())){
                return proxy;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public List<CommandProxy> getCommand(Command command, String parameters) {
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
    public void registerCommands(Class clazz) {
        //TODO
    }

    @Override
    public void registerCommands(Object object) {
        //TODO
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        //Put all of the arguments into a string to match
        StringBuilder stringBuilder = new StringBuilder();
        for(String arg : args){
            stringBuilder.append(arg).append(" ");
        }

        //get all the proxies that match the route
        List<CommandProxy> proxies = getCommand(command,stringBuilder.toString());

        //no proxies found that matched the route
        if(proxies == null){
            //TODO have a way to set message per command if no route exists
            return false;
        }

        //trigger all the proxies
        for(CommandProxy proxy : proxies){
            CommandRequestBuilder builder = m_requestProvider.get();
            CommandRequest request = builder.setCommand(command)
                    .setArguments(args)
                    .setSender(sender)
                    .build();
            proxy.trigger(request);
        }

        //don't print the error message
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] strings) {
        //TODO
        return null;
    }
}
