/*
 * PermissionRestrictedRoute.java
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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;

public class PermissionRestrictedRoute extends Route {

    private final String m_permission;

    /**
     * Make a route that restricts based on permission.
     * This route WILL show an error message if it fails, make sure it is deeper in the chain if this causes issues.
     * @param route the route to run after this route
     * @param permission the permission to check
     */
    public PermissionRestrictedRoute(Route route, String permission) {
        super(route);
        m_permission = permission;
    }

    @Override
    public RouteMatch matches(CommandSender sender, Command command, String arguments) {
        boolean matched = sender.hasPermission(m_permission);

        Set<String> errors = new HashSet<String>();
        if( !matched ) {
            errors.add(ChatColor.RED + "You don't have the permission " + ChatColor.BLUE + m_permission);
        }
        return new DefaultRouteMatch(matched, errors);
    }
}
