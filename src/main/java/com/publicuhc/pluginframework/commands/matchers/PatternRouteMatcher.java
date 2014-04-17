/*
 * PatternRouteMatcher.java
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

package com.publicuhc.pluginframework.commands.matchers;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternRouteMatcher implements RouteMatcher {

    private final Pattern m_pattern;
    private Matcher m_matcher;

    /**
     * A route that matches the pattern given
     * @param pattern the pattern to match against
     */
    public PatternRouteMatcher(Pattern pattern) {
        m_pattern = pattern;
    }

    @Override
    public MatchResult getResult(String match) {
        if(null == m_matcher) {
            m_matcher = m_pattern.matcher(match);
        } else {
            m_matcher.reset(match);
        }

        return m_matcher.matches() ? m_matcher.toMatchResult() : null;
    }
}
