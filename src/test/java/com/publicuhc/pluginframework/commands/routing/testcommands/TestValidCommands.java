/*
 * TestValidCommands.java
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

package com.publicuhc.pluginframework.commands.routing.testcommands;

import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.annotation.TabCompletion;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import com.publicuhc.pluginframework.commands.requests.SenderType;
import com.publicuhc.pluginframework.commands.routing.BaseMethodRoute;
import com.publicuhc.pluginframework.commands.routing.MethodRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.powermock.api.mockito.PowerMockito.mock;

public class TestValidCommands {

    @CommandMethod
    public void testCommand(CommandRequest request) {}

    @TabCompletion
    public List<String> testTabComplete(CommandRequest request) {
        return new ArrayList<String>();
    }

    @RouteInfo
    public MethodRoute testCommandDetails() {
        return new BaseMethodRoute(Pattern.compile("^arg$"), new SenderType[]{SenderType.CONSOLE, SenderType.REMOTE_CONSOLE}, "test.permission", "test");
    }

    @RouteInfo
    public MethodRoute testTabCompleteDetails() {
        return new BaseMethodRoute(Pattern.compile("^arg$"), new SenderType[]{SenderType.CONSOLE, SenderType.REMOTE_CONSOLE}, "test.permission", "test");
    }
}