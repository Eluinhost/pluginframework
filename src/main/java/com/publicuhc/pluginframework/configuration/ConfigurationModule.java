/*
 * ConfigurationModule.java
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

package com.publicuhc.pluginframework.configuration;

import com.google.inject.AbstractModule;

public class ConfigurationModule extends AbstractModule {

    private final ClassLoader m_classLoader;

    public ConfigurationModule(ClassLoader loader) {
        m_classLoader = loader;
    }

    @Override
    protected void configure() {
        bind(Configurator.class).to(DefaultConfigurator.class);
        bind(ClassLoader.class).toInstance(m_classLoader);
    }
}
