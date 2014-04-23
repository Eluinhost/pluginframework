/*
 * Router.java
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

import com.publicuhc.pluginframework.commands.exceptions.CommandClassParseException;
import com.publicuhc.pluginframework.commands.proxies.CommandProxy;
import com.publicuhc.pluginframework.commands.proxies.TabCompleteProxy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public interface Router extends TabExecutor {

    /**
     * @param command    the command string
     * @param parameters the parameters
     * @return the list of matched commandproxy
     */
    List<CommandProxy> getCommandProxy(CommandSender sender, Command command, String parameters);

    /**
     * @param command    the command string
     * @param parameters the parameters
     * @return the list of matched tabcompleteproxy
     */
    List<TabCompleteProxy> getTabCompleteProxy(CommandSender sender, Command command, String parameters);

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

    /**
     * Set the message to be displayed if a command is triggered but gets no matches
     * @param commandName the name of the command
     * @param message the messages to send
     */
    void setDefaultMessageForCommand(String commandName, List<String> message);

    /**
     * Set the message to be displayed if a command is triggered but gets no matches
     * @param commandName the name of the command
     * @param message the message to send
     */
    void setDefaultMessageForCommand(String commandName, String message);
}
