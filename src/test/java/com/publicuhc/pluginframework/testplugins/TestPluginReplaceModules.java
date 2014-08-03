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
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.publicuhc.pluginframework.FrameworkJavaPlugin;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.configuration.DefaultConfigurator;
import com.publicuhc.pluginframework.routing.DefaultRouter;
import com.publicuhc.pluginframework.routing.Router;
import com.publicuhc.pluginframework.routing.parser.DefaultRoutingMethodParser;
import com.publicuhc.pluginframework.routing.parser.RoutingMethodParser;
import com.publicuhc.pluginframework.translate.DefaultTranslate;
import com.publicuhc.pluginframework.translate.Translate;
import joptsimple.OptionParser;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

import java.io.File;
import java.util.ArrayList;
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

    @Inject
    public OptionParser parser;

    @Override
    public boolean initUseDefaultBindings() {
        return false;
    }

    @Override
    public List<AbstractModule> initialModules() {
        List<AbstractModule> modules = new ArrayList<AbstractModule>();
        AbstractModule module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(Router.class).to(TestConcreteRouter.class);
                bind(Configurator.class).to(TestConcreteConfigurator.class);
                bind(Translate.class).to(TestConcreteTranslate.class);
                bind(RoutingMethodParser.class).to(TestRoutingMethodParser.class);
                bind(ClassLoader.class).toInstance(getClass().getClassLoader());
            }
        };
        modules.add(module);
        return modules;
    }

    public static class TestConcreteRouter extends DefaultRouter
    {
        @Inject
        protected TestConcreteRouter(RoutingMethodParser parser, Injector injector)
        {
            super(parser, injector);
        }
    }

    public static class TestConcreteTranslate extends DefaultTranslate {
        @Inject
        protected TestConcreteTranslate(Configurator configurator) {
            super(configurator);
        }
    }

    public static class TestConcreteConfigurator extends DefaultConfigurator {
        @Inject
        public TestConcreteConfigurator(@Named("dataFolder") File file, ClassLoader loader) {
            super(file, loader);
        }
    }

    public static class TestRoutingMethodParser extends DefaultRoutingMethodParser {}
}
