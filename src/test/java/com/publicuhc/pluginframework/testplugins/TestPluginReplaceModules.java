/*
 * TestPluginReplaceModules.java
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

package com.publicuhc.pluginframework.testplugins;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.publicuhc.pluginframework.FrameworkJavaPlugin;
import com.publicuhc.pluginframework.PluginModule;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginLogger;

import java.io.File;
import java.util.List;

public class TestPluginReplaceModules extends FrameworkJavaPlugin {

    /**
     * This method is intended for unit testing purposes. Its existence may be temporary.
     *
     * @see org.bukkit.plugin.java.JavaPlugin
     */
    public TestPluginReplaceModules(PluginLoader loader, Server server, PluginDescriptionFile pdf, File file1, File file2) {
        super(loader, server, pdf, file1, file2);
    }

    @Override
    protected void initialModules(List<Module> modules)
    {
        AbstractModule module = new AbstractModule() {
            @Override
            protected void configure()
            {
                bind(PluginLogger.class).to(TestPluginLogger.class);
            }
        };
        Module overriden = Modules.override(new PluginModule(this)).with(module);
        modules.add(overriden);
    }

    public PluginLogger logger;

    @Inject
    private void setLogger(PluginLogger logger)
    {
        this.logger = logger;
    }

    public static class TestPluginLogger extends PluginLogger
    {
        @Inject
        public TestPluginLogger(Plugin context)
        {
            super(context);
        }
    }
}
