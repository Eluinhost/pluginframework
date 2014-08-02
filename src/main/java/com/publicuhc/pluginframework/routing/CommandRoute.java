/*
 * CommandRoute.java
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

import com.publicuhc.pluginframework.routing.exception.CommandInvocationException;
import com.publicuhc.pluginframework.routing.parser.CommandOptionsParser;
import com.publicuhc.pluginframework.routing.proxy.MethodProxy;
import joptsimple.OptionException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CommandRoute
{

    /**
     * @return the option details that define the allowed options
     */
    public CommandOptionsParser getOptionDetails();

    /**
     * @return the proxy to run on command trigger
     */
    public MethodProxy getProxy();

    /**
     * @return the name of the command to run on
     */
    public String getCommandName();

    /**
     * @return permission required to run the command or null if no permission required
     */
    public String getPermission();

    /**
     * Run this command route
     *
     * @param args the args for the method
     * @throws org.bukkit.craftbukkit.libs.joptsimple.OptionException                     when options do not match expected
     * @throws com.publicuhc.pluginframework.routing.exception.CommandInvocationException if exception thrown in command
     */
    public void run(Command command, CommandSender sender, String[] args) throws OptionException, CommandInvocationException;
}
