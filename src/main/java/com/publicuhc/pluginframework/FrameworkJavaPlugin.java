/*
 * FrameworkJavaPlugin.java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.publicuhc.pluginframework;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.publicuhc.pluginframework.configuration.ConfigurationModule;
import com.publicuhc.pluginframework.locale.LocaleModule;
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
        defaults.add(new LocaleModule());
        defaults.add(new TranslateModule());
        defaults.add(new MetricsModule());
        defaults.add(new ConfigurationModule(this.getClassLoader()));
        defaults.add(new RoutingModule());
        return defaults;
    }
}
