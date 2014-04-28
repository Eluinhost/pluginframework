/*
 * ArgumentRestrictedRoute.java
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

package com.publicuhc.pluginframework.commands.routes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ArgumentRestrictedRoute extends Route {

    private final int m_min;
    private final int m_max;

    /**
     * if either min or max is set to -1 it will be ignored
     * @param route the parent route
     * @param min the minimum allowed
     * @param max the maximum allowed
     */
    public ArgumentRestrictedRoute(Route route, int min, int max) {
        super(route);
        m_min = min;
        m_max = max;
    }

    @Override
    public boolean matches(CommandSender sender, Command command, String arguments) {
        String[] tokens = arguments.split(" ");
        return (m_min < 0 || tokens.length >= m_min) && (m_max < 0 || tokens.length <= m_max) && getNextChain().matches(sender, command, arguments);
    }
}
