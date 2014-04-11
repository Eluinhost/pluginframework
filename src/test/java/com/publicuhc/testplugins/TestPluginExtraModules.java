/*
 * TestPluginExtraModules.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
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

package com.publicuhc.testplugins;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.publicuhc.FrameworkJavaPlugin;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestPluginExtraModules extends FrameworkJavaPlugin {

    /**
     * This method is intended for unit testing purposes. Its existence may be temporary.
     * @see org.bukkit.plugin.java.JavaPlugin
     */
    public TestPluginExtraModules(PluginLoader loader, Server server, PluginDescriptionFile pdf, File file1, File file2) {
        super(loader, server, pdf, file1, file2);
    }

    public TestInterface i;
    @Inject
    public void setTest(TestInterface i) {
        this.i = i;
    }

    @Override
    public List<AbstractModule> initialModules() {
        List<AbstractModule> modules = new ArrayList<AbstractModule>();
        AbstractModule module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(TestInterface.class).to(TestConcrete.class);
            }
        };
        modules.add(module);
        return modules;
    }

    public static interface TestInterface {}
    public static class TestConcrete implements TestInterface {}
}
