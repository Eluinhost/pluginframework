/*
 * PluginModule.java
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

package com.publicuhc.pluginframework;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.publicuhc.pluginframework.metrics.PluginMetrics;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.mcstats.Metrics;

import java.io.File;

public class PluginModule extends AbstractModule {

    private final Plugin m_plugin;

    public PluginModule(Plugin plugin) {
        m_plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(File.class).annotatedWith(Names.named("dataFolder")).toInstance(m_plugin.getDataFolder());
        bind(Plugin.class).toInstance(m_plugin);
        bind(PluginLogger.class).to(PluginLoggerInjectable.class);
        bind(Metrics.class).to(PluginMetrics.class);
    }
}
