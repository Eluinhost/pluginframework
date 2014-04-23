/*
 * BaseRoute.java
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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BaseRoute extends Route {

    private final int m_maxMatches;

    public BaseRoute(int maxMatches) {
        super(null);
        m_maxMatches = maxMatches;
    }

    public BaseRoute() {
        super(null);
        m_maxMatches = 0;
    }

    @Override
    public int getMaxMatches() {
        return m_maxMatches;
    }

    @Override
    public boolean matches(CommandSender sender, Command command, String arguments) {
        return true;
    }
}
