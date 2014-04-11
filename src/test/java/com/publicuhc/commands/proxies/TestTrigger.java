/*
 * TestTrigger.java
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

package com.publicuhc.commands.proxies;

import com.publicuhc.commands.requests.CommandRequest;

import java.util.Arrays;
import java.util.List;

public class TestTrigger {

    public static final List<String> args = Arrays.asList("a", "b", "c");

    public void triggerCommandMethod(CommandRequest request) {
    }

    public List<String> triggerTabComplete(CommandRequest request) {
        return args;
    }

    public void triggerException(CommandRequest request) {
    }
}
