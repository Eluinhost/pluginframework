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
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
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
        assertThat(baseRoute.allMatch(mock(CommandSender.class), mock(Command.class), "").matches()).isTrue();
    }

    @Test
    public void testArgumentRestrictedRoute() {
        ArgumentRestrictedRoute route = new ArgumentRestrictedRoute(baseRoute, 1, 2);

        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "one two").matches()).isTrue();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "one").matches()).isTrue();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "one two three").matches()).isFalse();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "").matches()).isFalse();

        route = new ArgumentRestrictedRoute(baseRoute, 0, -1);

        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "one two").matches()).isTrue();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "one").matches()).isTrue();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "one two three").matches()).isTrue();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "").matches()).isTrue();

        route = new ArgumentRestrictedRoute(baseRoute, -1, 0);

        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "one two").matches()).isFalse();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "one").matches()).isFalse();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "one two three").matches()).isFalse();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "").matches()).isTrue();
    }

    @Test
    public void testStartsWithRestrictedRoute() {
        StartsWithRestrictedRoute route = new StartsWithRestrictedRoute(baseRoute, "*");

        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "*one two").matches()).isTrue();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "* one").matches()).isTrue();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "*").matches()).isTrue();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "one two three").matches()).isFalse();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "").matches()).isFalse();

        route = new StartsWithRestrictedRoute(baseRoute, "OnE");

        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "one two").matches()).isTrue();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "ONE").matches()).isTrue();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "One two").matches()).isTrue();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "two one three").matches()).isFalse();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "").matches()).isFalse();
    }

    @Test
    public void testCommandRestrictedRoute() {
        Command valid = mock(Command.class);
        when(valid.getName()).thenReturn("valid");

        Command invalid = mock(Command.class);
        when(invalid.getName()).thenReturn("invalid");

        CommandRestrictedRoute route = new CommandRestrictedRoute(baseRoute, "valid");

        assertThat(route.allMatch(mock(CommandSender.class), valid, "").matches()).isTrue();
        assertThat(route.allMatch(mock(CommandSender.class), invalid, "").matches()).isFalse();
    }

    @Test
    public void testPatternRestrictedRoute() {
        PatternRestrictedRoute route = new PatternRestrictedRoute(baseRoute, Pattern.compile("(valid|good)"));

        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "valid").matches()).isTrue();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "good").matches()).isTrue();
        assertThat(route.allMatch(mock(CommandSender.class), mock(Command.class), "invalid").matches()).isFalse();
    }

    @Test
    public void testPermissionRestrictedRoute() {
        CommandSender goodsender = mock(CommandSender.class);
        when(goodsender.hasPermission(anyString())).thenReturn(true);

        CommandSender badsender = mock(CommandSender.class);
        when(badsender.hasPermission(anyString())).thenReturn(false);

        PermissionRestrictedRoute route = new PermissionRestrictedRoute(baseRoute, "test.permission");

        assertThat(route.allMatch(goodsender, mock(Command.class), "").matches()).isTrue();
        assertThat(route.allMatch(badsender, mock(Command.class), "").matches()).isFalse();
        Set<String> error = new HashSet<String>();
        error.add(ChatColor.RED + "You don't have the permission " + ChatColor.BLUE + "test.permission");
        assertThat(route.allMatch(badsender, mock(Command.class), "").getErrorMessages()).isEqualTo(error);
    }

    @Test
    public void testSenderTypeRestrictedRoute() {
        Player player = mock(Player.class);
        BlockCommandSender block = mock(BlockCommandSender.class);

        SenderTypeRestrictedRoute route = new SenderTypeRestrictedRoute(baseRoute, SenderType.PLAYER);

        assertThat(route.allMatch(player, mock(Command.class), "").matches()).isTrue();
        assertThat(route.allMatch(block, mock(Command.class), "").matches()).isFalse();
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
        assertThat(permissionRestricted.allMatch(goodPlayer, valid, "").matches()).isTrue();

        //test bad permission
        assertThat(permissionRestricted.allMatch(badPlayer, valid, "").matches()).isFalse();

        //test bad type
        assertThat(permissionRestricted.allMatch(block, valid, "").matches()).isFalse();

        //test bad command
        assertThat(permissionRestricted.allMatch(goodPlayer, invalid, "").matches()).isFalse();
    }
}
