/*
 * BaseRouteTest.java
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
import com.publicuhc.pluginframework.commands.routes.*;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class RouteTest {

    private Route baseRoute;

    @Before
    public void onStartUp() {
        baseRoute = new BaseRoute();
    }

    @Test
    public void testBaseRoute() {
        assertTrue(baseRoute.matches(mock(CommandSender.class), mock(Command.class), ""));
    }

    @Test
    public void testCommandRestrictedRoute() {
        Command valid = mock(Command.class);
        when(valid.getName()).thenReturn("valid");

        Command invalid = mock(Command.class);
        when(invalid.getName()).thenReturn("invalid");

        CommandRestrictedRoute route = new CommandRestrictedRoute(baseRoute, "valid");

        assertTrue(route.matches(mock(CommandSender.class), valid, ""));
        assertFalse(route.matches(mock(CommandSender.class), invalid, ""));
    }

    @Test
    public void testPatternRestrictedRoute() {
        PatternRestrictedRoute route = new PatternRestrictedRoute(baseRoute, Pattern.compile("(valid|good)"));

        assertTrue(route.matches(mock(CommandSender.class), mock(Command.class), "valid"));
        assertTrue(route.matches(mock(CommandSender.class), mock(Command.class), "good"));
        assertFalse(route.matches(mock(CommandSender.class), mock(Command.class), "invalid"));
    }

    @Test
    public void testPermissionRestrictedRoute() {
        CommandSender goodsender = mock(CommandSender.class);
        when(goodsender.hasPermission(anyString())).thenReturn(true);

        CommandSender badsender = mock(CommandSender.class);
        when(badsender.hasPermission(anyString())).thenReturn(false);

        PermissionRestrictedRoute route = new PermissionRestrictedRoute(baseRoute, "test.permission");

        assertTrue(route.matches(goodsender, mock(Command.class), ""));
        assertFalse(route.matches(badsender, mock(Command.class), ""));
    }

    @Test
    public void testSenderTypeRestrictedRoute() {
        Player player = mock(Player.class);
        BlockCommandSender block = mock(BlockCommandSender.class);

        SenderTypeRestrictedRoute route = new SenderTypeRestrictedRoute(baseRoute, SenderType.PLAYER);

        assertTrue(route.matches(player, mock(Command.class), ""));
        assertFalse(route.matches(block, mock(Command.class), ""));
    }

    @Test
    public void testMultipleLayers() {
        Player goodPlayer = mock(Player.class);
        when(goodPlayer.hasPermission(anyString())).thenReturn(true);

        Player badPlayer = mock(Player.class);
        when(badPlayer.hasPermission(anyString())).thenReturn(false);

        BlockCommandSender block = mock(BlockCommandSender.class);
        when(block.hasPermission(anyString())).thenReturn(true);

        Command valid = mock(Command.class);
        when(valid.getName()).thenReturn("valid");

        Command invalid = mock(Command.class);
        when(invalid.getName()).thenReturn("invalid");

        SenderTypeRestrictedRoute senderRestricted = new SenderTypeRestrictedRoute(baseRoute, SenderType.PLAYER);
        CommandRestrictedRoute commandRestricted = new CommandRestrictedRoute(senderRestricted, "valid");
        PermissionRestrictedRoute permissionRestricted = new PermissionRestrictedRoute(commandRestricted, "test.permission");

        //test valid first
        assertTrue(permissionRestricted.matches(goodPlayer, valid, ""));

        //test bad permission
        assertFalse(permissionRestricted.matches(badPlayer, valid, ""));

        //test bad type
        assertFalse(permissionRestricted.matches(block, valid, ""));

        //test bad command
        assertFalse(permissionRestricted.matches(goodPlayer, invalid, ""));
    }
}
