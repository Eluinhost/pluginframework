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
import io.github.reggert.reb4j.Expression;

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
    public RouteBuilder restrictCommand(String string) {
        m_route = new CommandRestrictedRoute(m_route, string);
        return this;
    }

    @Override
    public RouteBuilder restrictPermission(String permission) {
        m_route = new PermissionRestrictedRoute(m_route, permission);
        return this;
    }

    @Override
    public RouteBuilder restrictPattern(Expression expression) {
        m_route = new PatternRestrictedRoute(m_route, expression.toPattern());
        return this;
    }

    @Override
    public RouteBuilder restrictSenderType(SenderType... types) {
        m_route = new SenderTypeRestrictedRoute(m_route, types);
        return this;
    }

    @Override
    public RouteBuilder maxMatches(int matches) {
        m_baseRoute.setMaxMatches(matches);
        return this;
    }

    @Override
    public RouteBuilder reset() {
        m_baseRoute = new BaseRoute();
        m_route = m_baseRoute;
        return this;
    }
}
