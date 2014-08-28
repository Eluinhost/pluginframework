/*
 * SampleCommand.java
 *
 * Copyright (c) 2014. Graham Howden <graham_howden1 at yahoo.co.uk>.
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

package com.publicuhc.pluginframework.routing.testcommands;

import com.publicuhc.pluginframework.routing.CommandMethod;
import com.publicuhc.pluginframework.routing.OptionsMethod;
import joptsimple.OptionDeclarer;
import joptsimple.OptionSet;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SampleCommand
{
    public Double radius;
    public List<String> arguments;
    public OptionSet set;

    @CommandMethod(command = "test", options = true, optionOrder = {"r", "[arguments]"})
    public void testMethod(OptionSet set, CommandSender sender, Double radius, List<String> arguments)
    {
        this.set = set;
        this.radius = radius;
        this.arguments = arguments;
    }

    @OptionsMethod
    public void testMethod(OptionDeclarer parser)
    {
        parser.accepts("r")
                .withRequiredArg()
                .describedAs("an argument")
                .defaultsTo("default")
                .ofType(Double.class)
                .required();
        parser.accepts("b")
                .withOptionalArg();
    }
}