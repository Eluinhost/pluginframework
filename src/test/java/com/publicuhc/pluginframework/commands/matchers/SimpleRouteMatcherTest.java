/*
 * SimpleRouteMatcherTest.java
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.regex.MatchResult;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
public class SimpleRouteMatcherTest {

    @Test
    public void testMatches() {
        String testString = "test string\\ with $ , . metachars";
        SimpleRouteMatcher matcher = new SimpleRouteMatcher(testString);

        MatchResult result = matcher.getResult(testString);

        assertThat(result, is(not(nullValue())));
        assertThat(result.group(0), is(equalTo(testString)));

        result = matcher.getResult("new string");
        assertThat(result, is(nullValue()));
    }
}
