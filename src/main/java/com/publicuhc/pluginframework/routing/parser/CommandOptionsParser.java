/*
 * CommandOptionsParser.java
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

package com.publicuhc.pluginframework.routing.parser;

import org.bukkit.craftbukkit.libs.joptsimple.OptionParser;
import org.bukkit.craftbukkit.libs.joptsimple.OptionSet;

public interface CommandOptionsParser
{

    public String[] getRequiredOptions();

    public OptionParser getParser();

    /**
     * Proxy for {@link org.bukkit.craftbukkit.libs.joptsimple.OptionParser#parse(String...)} but also checks for all options set
     *
     * @param args the args to parse
     * @return the parsed optionset
     * @throws org.bukkit.craftbukkit.libs.joptsimple.OptionException                 when failing parsing the args or required args are not present
     * @throws com.publicuhc.pluginframework.routing.exception.OptionMissingException when a required option is missing
     */
    public OptionSet parse(String... args);
}
