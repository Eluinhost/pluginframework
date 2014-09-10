/*
 * Router.java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
