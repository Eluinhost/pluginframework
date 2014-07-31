/*
 * PluginMetrics.java
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

package com.publicuhc.pluginframework.metrics;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.plugin.Plugin;
import org.mcstats.Metrics;

import java.io.IOException;

@Singleton
public class PluginMetrics extends Metrics {

    /**
     * Construct some UHC metrics
     * @param plugin the plugin to use
     * @throws java.io.IOException when fails or something
     */
    @Inject
    private PluginMetrics(Plugin plugin) throws IOException {
        super(plugin);
    }
}
