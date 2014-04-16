/*
 * BaseMethodRoute.java
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

import com.publicuhc.pluginframework.commands.requests.SenderType;

import java.util.regex.Pattern;

public class BaseMethodRoute implements MethodRoute {

    private final Pattern m_route;
    private final SenderType[] m_allowedTypes;
    private final String m_permission;
    private final String m_baseCommand;

    /**
     * @param route        the pattern to match to run
     * @param allowedTypes the allowed sender types for running
     * @param permission   the permission needed to run
     * @param baseCommand  the base command to run under
     */
    public BaseMethodRoute(Pattern route, SenderType[] allowedTypes, String permission, String baseCommand) {
        m_route = route;
        m_allowedTypes = allowedTypes;
        m_permission = permission;
        m_baseCommand = baseCommand;
    }

    @Override
    public Pattern getRoute() {
        return m_route;
    }

    @Override
    public SenderType[] getAllowedTypes() {
        return m_allowedTypes;
    }

    @Override
    public String getPermission() {
        return m_permission;
    }

    @Override
    public String getBaseCommand() {
        return m_baseCommand;
    }
}
