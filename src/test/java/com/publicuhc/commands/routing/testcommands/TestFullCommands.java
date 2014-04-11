/*
 * TestFullCommands.java
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

package com.publicuhc.commands.routing.testcommands;

import com.publicuhc.commands.annotation.CommandMethod;
import com.publicuhc.commands.annotation.RouteInfo;
import com.publicuhc.commands.annotation.TabCompletion;
import com.publicuhc.commands.requests.CommandRequest;
import com.publicuhc.commands.requests.SenderType;
import com.publicuhc.commands.routing.BaseMethodRoute;
import com.publicuhc.commands.routing.MethodRoute;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TestFullCommands {

    @CommandMethod
    public void banIP(CommandRequest request) {
        Bukkit.banIP(request.getArg(0));
        request.sendMessage("banned with message "+request.getMatcherResult().group(2));
    }

    @RouteInfo
    public MethodRoute banIPDetails() {
        return new BaseMethodRoute(
                Pattern.compile("^([\\d]{1,3}.[\\d]{1,3}.[\\d]{1,3}.[\\d]{1,3}) (.*)$"),
                new SenderType[]{SenderType.CONSOLE},
                "test.permission",
                "banIP"
        );
    }

    @TabCompletion
    public List<String> complete(CommandRequest request) {
        List<String> complete = new ArrayList<String>();
        complete.add("1");
        complete.add("2");
        complete.add("3");
        return complete;
    }

    @RouteInfo
    public MethodRoute completeDetails() {
        return new BaseMethodRoute(
                Pattern.compile("^([\\d]{1,3}.[\\d]{1,3}.[\\d]{1,3}.[\\d]{1,3})$"),
                new SenderType[]{SenderType.CONSOLE},
                "test.permission",
                "banIP");
    }
}
