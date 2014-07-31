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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.publicuhc.pluginframework.configuration.ConfigurationModule;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.routing.Router;
import com.publicuhc.pluginframework.routing.RoutingModule;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.pluginframework.translate.TranslateModule;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class FrameworkJavaPlugin extends JavaPlugin {

    private Configurator m_configurator;
    private Translate m_translate;
    private Router m_router;
    private Metrics m_metrics;

    /**
     * This method is intended for unit testing purposes. Its existence may be temporary.
     *
     * @see org.bukkit.plugin.java.JavaPlugin
     */
    protected FrameworkJavaPlugin(PluginLoader loader, Server server, PluginDescriptionFile pdf, File file1, File file2) {
        super(loader, server, pdf, file1, file2);
    }

    public FrameworkJavaPlugin() {}

    @Override
    public final void onEnable() {
        List<AbstractModule> modules = initialModules();
        if (modules == null) {
            modules = new ArrayList<AbstractModule>();
        }
        modules.add(new PluginModule(this));
        if (initUseDefaultBindings()) {
            modules.add(new RoutingModule());
            modules.add(new ConfigurationModule(this.getClassLoader()));
            modules.add(new TranslateModule());
        }
        Injector injector = Guice.createInjector(modules);
        injector.injectMembers(this);
        onFrameworkEnable();
    }

    /**
     * Called when the framework is loaded
     */
    public void onFrameworkEnable() {
    }

    /**
     * Return a list of extra modules to initialize DI with.
     * Override this method to change the extra modules to load with.
     *
     * @return the list of modules
     */
    public List<AbstractModule> initialModules() {
        return null;
    }

    /**
     * If returns true will include default modules on init.
     * If false you must specify all the bindings for the framework
     *
     * @return whether to use the defaults or not
     */
    public boolean initUseDefaultBindings() {
        return true;
    }

    @Inject
    private void setMetrics(Metrics metrics) {
        m_metrics = metrics;
    }

    /**
     * <b>ONLY USE THIS AFTER onEnable HAS BEEN CALLED. (onFrameworkEnable and later) otherwise it will return null</b>
     *
     * @return the plugin metrics
     */
    public Metrics getMetrics() {
        return m_metrics;
    }

    /**
     * Set the router by injection
     *
     * @param router the router object
     */
    @Inject
    private void setRouter(Router router) {
        m_router = router;
    }

    /**
     * Set the configurator by injection
     *
     * @param configurator the configurator object
     */
    @Inject
    private void setConfigurator(Configurator configurator) {
        m_configurator = configurator;
    }

    /**
     * Set the translate by injection
     *
     * @param translate the translate object
     */
    @Inject
    private void setTranslate(Translate translate) {
        m_translate = translate;
    }

    /**
     * <b>ONLY USE THIS AFTER onLoad HAS BEEN CALLED. (onFrameworkEnable and later) otherwise it will return null</b>
     *
     * @return the router object
     */
    public Router getRouter() {
        return m_router;
    }

    /**
     * <b>ONLY USE THIS AFTER onLoad HAS BEEN CALLED. (onFrameworkEnable and later) otherwise it will return null</b>
     *
     * @return the configurator object
     */
    public Configurator getConfigurator() {
        return m_configurator;
    }

    /**
     * <b>ONLY USE THIS AFTER onEnable HAS BEEN CALLED. (onFrameworkEnable and later) otherwise it will return null</b>
     *
     * @return the translate object
     */
    public Translate getTranslate() {
        return m_translate;
    }
}
