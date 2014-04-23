/*
 * TestExceptionCommandMethod.java
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
import com.publicuhc.pluginframework.commands.routing.RouteBuilder;

import java.util.List;

public class TestExceptionCommandMethod {

    @CommandMethod
    public void exception(CommandRequest request) {
        throw new IllegalAccessError();
    }

    @RouteInfo
    public void exceptionDetails(RouteBuilder builder) {
        builder.restrictCommand("test");
    }

    @TabCompletion
    public List<String> tabexception(CommandRequest request) {
        throw new IllegalAccessError();
    }

    @RouteInfo
    public void tabexceptionDetails(RouteBuilder builder) {
        builder.restrictCommand("test");
    }
}
