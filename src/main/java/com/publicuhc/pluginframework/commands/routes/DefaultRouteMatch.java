/*
 * DefaultRouteMatch.java
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

import java.util.HashSet;
import java.util.Set;

public class DefaultRouteMatch implements RouteMatch {

    private final boolean m_matches;
    private final Set<String> m_messages;

    public DefaultRouteMatch(boolean matched, Set<String> errorMessages) {
        m_matches = matched;
        m_messages = errorMessages;
    }

    public DefaultRouteMatch(boolean matched) {
        m_matches = matched;
        m_messages = new HashSet<String>();
    }

    @Override
    public boolean matches() {
        return m_matches;
    }

    @Override
    public Set<String> getErrorMessages() {
        return m_messages;
    }
}
