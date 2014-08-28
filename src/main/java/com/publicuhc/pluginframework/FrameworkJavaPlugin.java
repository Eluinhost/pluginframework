/*
 * FrameworkJavaPlugin.java
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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.publicuhc.pluginframework.configuration.ConfigurationModule;
import com.publicuhc.pluginframework.metrics.MetricsModule;
import com.publicuhc.pluginframework.routing.RoutingModule;
import com.publicuhc.pluginframework.translate.TranslateModule;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class FrameworkJavaPlugin extends JavaPlugin {

    /**
     * This method is intended for unit testing purposes. Its existence may be temporary.
     *
     * @see org.bukkit.plugin.java.JavaPlugin
     */
    @SuppressWarnings("deprecation")
    protected FrameworkJavaPlugin(PluginLoader loader, Server server, PluginDescriptionFile pdf, File file1, File file2)
    {
        super(loader, server, pdf, file1, file2);
    }

    public FrameworkJavaPlugin() {}

    @Override
    public final void onEnable()
    {
        List<Module> modules = new ArrayList<Module>();
        initialModules(modules);

        Injector injector = Guice.createInjector(modules);
        injector.injectMembers(this);
        onFrameworkEnable();
    }

    /**
     * Called when the framework is loaded
     */
    protected void onFrameworkEnable()
    {
    }

    /**
     * Setup the list of modules to load for the DI
     */
    protected abstract void initialModules(List<Module> modules);

    protected List<Module> getDefaultModules()
    {
        List<Module> defaults = new ArrayList<Module>();
        defaults.add(new PluginModule(this));
        defaults.add(new TranslateModule());
        defaults.add(new MetricsModule());
        defaults.add(new ConfigurationModule(this.getClassLoader()));
        defaults.add(new RoutingModule());
        return defaults;
    }
}
