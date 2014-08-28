/*
 * TestPluginDefaultModules.java
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

import com.google.inject.Inject;
import com.google.inject.Module;
import com.publicuhc.pluginframework.FrameworkJavaPlugin;
import com.publicuhc.pluginframework.PluginModule;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

import java.io.File;
import java.util.List;

public class TestPluginNoModules extends FrameworkJavaPlugin {

    public Plugin injectedPlugin;

    /**
     * This method is intended for unit testing purposes. Its existence may be temporary.
     *
     * @see org.bukkit.plugin.java.JavaPlugin
     */
    public TestPluginNoModules(PluginLoader loader, Server server, PluginDescriptionFile pdf, File file1, File file2)
    {
        super(loader, server, pdf, file1, file2);
    }

    @Override
    protected void initialModules(List<Module> modules)
    {
        modules.add(new PluginModule(this));
    }

    @Inject
    public void setInjectedPlugin(Plugin plugin)
    {
        this.injectedPlugin = plugin;
    }
}
