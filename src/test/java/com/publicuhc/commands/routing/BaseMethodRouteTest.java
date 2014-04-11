/*
 * BaseMethodRouteTest.java
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

package com.publicuhc.commands.routing;

import com.publicuhc.commands.requests.SenderType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
public class BaseMethodRouteTest {

    private Pattern pattern;
    private SenderType[] allowed;
    private String perm;
    private String baseCommand;

    private BaseMethodRoute route;

    @Test
    public void testRoute() {
        assertThat(route.getRoute(), is(sameInstance(pattern)));
    }

    @Test
    public void testAllowedTypes() {
        assertThat(route.getAllowedTypes(), is(sameInstance(allowed)));
    }

    @Test
    public void testPermission() {
        assertThat(route.getPermission(), is(sameInstance(perm)));
    }

    @Test
    public void testBaseCommand() {
        assertThat(route.getBaseCommand(), is(sameInstance(baseCommand)));
    }

    @Before
    public void onStartUp() {
        pattern = mock(Pattern.class);
        allowed = new SenderType[]{SenderType.PLAYER, SenderType.CONSOLE};
        perm = "perms";
        baseCommand = "testcommand";

        route = new BaseMethodRoute(pattern, allowed, perm, baseCommand);
    }
}
