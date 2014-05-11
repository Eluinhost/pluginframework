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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class SenderTypeRestrictedRoute extends Route {

    private final SenderType[] m_types;

    public SenderTypeRestrictedRoute(Route route, SenderType... types) {
        super(route);
        m_types = types;
    }

    @Override
    public boolean matches(CommandSender sender, Command command, String arguments) {
        SenderType type = SenderType.getFromCommandSender(sender);

        return Arrays.asList(m_types).contains(type);
    }
}
