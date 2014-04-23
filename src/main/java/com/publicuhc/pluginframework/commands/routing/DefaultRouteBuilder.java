/*
 * DefaultRouteBuilder.java
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

public class DefaultRouteBuilder implements RouteBuilder {

    private Route m_route;
    private BaseRoute m_baseRoute;

    public DefaultRouteBuilder() {
        reset();
    }

    @Override
    public Route build() {
        return m_route;
    }

    @Override
    public void restrictCommand(String string) {
        m_route = new CommandRestrictedRoute(m_route, string);
    }

    @Override
    public void restrictPermission(String permission) {
        m_route = new PermissionRestrictedRoute(m_route, permission);
    }

    @Override
    public void restrictPattern(Pattern pattern) {
        m_route = new PatternRestrictedRoute(m_route, pattern);
    }

    @Override
    public void restrictSenderType(SenderType... types) {
        m_route = new SenderTypeRestrictedRoute(m_route, types);
    }

    @Override
    public void maxMatches(int matches) {
        m_baseRoute.setMaxMatches(matches);
    }

    @Override
    public void reset() {
        m_baseRoute = new BaseRoute();
        m_route = m_baseRoute;
    }
}
