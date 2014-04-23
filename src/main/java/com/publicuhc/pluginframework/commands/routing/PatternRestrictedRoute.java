/*
 * PatternRestrictedRoute.java
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternRestrictedRoute implements Route {

    private final Route m_wrapped;
    private final Pattern m_pattern;

    private Matcher m_matcher;

    public PatternRestrictedRoute(Route route, Pattern pattern) {
        m_wrapped = route;
        m_pattern = pattern;
    }

    @Override
    public boolean matches(CommandSender sender, Command command, String arguments) {
        //check arguments against the pattern
        if(null == m_matcher) {
            m_matcher = m_pattern.matcher(arguments);
        } else {
            m_matcher.reset(arguments);
        }

        //if the pattern wasn't correct this route chain fails
        return m_matcher.matches() && m_wrapped.matches(sender, command, arguments);
    }
}
