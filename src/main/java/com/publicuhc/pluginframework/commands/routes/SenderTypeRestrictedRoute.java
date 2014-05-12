/*
 * SenderTypeRestrictedRoute.java
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

import com.publicuhc.pluginframework.commands.requests.SenderType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SenderTypeRestrictedRoute extends Route {

    private final List<SenderType> m_types;

    private final String m_errorMessage;

    /**
     * Make a route that restricts based on sender type.
     * This route WILL show an error message if it fails, make sure it is deeper in the chain if this causes issues.
     * @param route the route to run after this route
     * @param types the allowed sender types
     */
    public SenderTypeRestrictedRoute(Route route, SenderType... types) {
        super(route);
        m_types = Arrays.asList(types);

        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.RED);
        builder.append("You must run this command as one of the following: ");
        for (SenderType type : types) {
            builder.append(type.name());
            builder.append(" ");
        }
        m_errorMessage = builder.toString();
    }

    @Override
    public RouteMatch matches(CommandSender sender, Command command, String arguments) {
        SenderType type = SenderType.getFromCommandSender(sender);

        boolean matched = m_types.contains(type);

        Set<String> errors = new HashSet<String>();
        if (!matched) {
            errors.add(m_errorMessage);
        }

        return new DefaultRouteMatch(matched, errors);
    }
}
