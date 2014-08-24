/*
 * Router.java
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
import com.publicuhc.pluginframework.routing.exception.CommandParseException;
import org.bukkit.command.TabExecutor;

import java.util.List;

public interface Router extends TabExecutor
{

    /**
     * Register a class for commands. Creates a new instance of said class and injects it using the default DI injector.
     *
     * @param klass the class to register commands for
     * @return the created object after injection
     * @throws com.publicuhc.pluginframework.routing.exception.CommandParseException when failing to parse any commands in the class
     */
    Object registerCommands(Class klass) throws CommandParseException;

    /**
     * Register a class for commands. Creates a new instance of said class and injects it using the default DI injector
     * with the provided modules added
     *
     * @param klass the class to register commands for
     * @param modules the extra modules to add to the injector
     * @return the created object after injection
     * @throws com.publicuhc.pluginframework.routing.exception.CommandParseException when failing to parse any commands in the class
     */
    Object registerCommands(Class klass, List<AbstractModule> modules) throws CommandParseException;

    /**
     * Register an instance for commands, optionally injecting using the DI injector
     * <b>
     * If injecting dependencies construction/property injection doesn't happen,
     * use {@link #registerCommands(Class)} to do constructor/property injection</b>
     * </b>
     *
     * @param object the object to register commands for
     * @param inject whether to inject dependencies or not (no constructor injection)
     * @throws com.publicuhc.pluginframework.routing.exception.CommandParseException when failing to parse any commands in the class
     */
    void registerCommands(Object object, boolean inject) throws CommandParseException;

    /**
     * Register an instance for commands, optionally injecting using the DI injector with the extra provided modules
     * <b>
     * If injecting dependencies construction/property injection doesn't happen,
     * use {@link #registerCommands(Class, List)} to do constructor/property injection</b>
     * </b>
     *
     * @param object the object to register commands for
     * @param inject whether to inject dependencies or not (no constructor injection)
     * @throws com.publicuhc.pluginframework.routing.exception.CommandParseException when failing to parse any commands in the class
     */
    void registerCommands(Object object, boolean inject, List<AbstractModule> modules) throws CommandParseException;

    /**
     * Set the messages to be displayed if a command is triggered but has no method to call
     *
     * @param commandName the name of the command
     * @param message     the messages to send
     */
    void setDefaultMessageForCommand(String commandName, List<String> message);

    /**
     * Set a message to be displayed if a command is triggered but has no method to call.
     *
     * @param commandName the name of the command
     * @param message     the message to send
     * @see #setDefaultMessageForCommand(String, java.util.List)
     */
    void setDefaultMessageForCommand(String commandName, String message);
}
