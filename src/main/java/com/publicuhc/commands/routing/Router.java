package com.publicuhc.commands.routing;

import com.publicuhc.commands.exceptions.CommandClassParseException;
import com.publicuhc.commands.proxies.CommandProxy;
import com.publicuhc.commands.proxies.TabCompleteProxy;
import org.bukkit.command.Command;
import org.bukkit.command.TabExecutor;

import java.util.Map;
import java.util.regex.MatchResult;

public interface Router extends TabExecutor {

    /**
     * @param command    the command string
     * @param parameters the parameters
     * @return the list of matched commandproxy
     */
    Map<CommandProxy, MatchResult> getCommandProxy(Command command, String parameters);

    /**
     * @param command    the command string
     * @param parameters the parameters
     * @return the list of matched tabcompleteproxy
     */
    Map<TabCompleteProxy, MatchResult> getTabCompleteProxy(Command command, String parameters);

    /**
     * Register a class for commands, makes an instance
     *
     * @param klass the class to register commands for
     * @return Object - the created class
     */
    Object registerCommands(Class klass) throws CommandClassParseException;

    /**
     * @param object the object to register commands for
     * @param inject whether to inject dependencies or not
     */
    void registerCommands(Object object, boolean inject) throws CommandClassParseException;
}
