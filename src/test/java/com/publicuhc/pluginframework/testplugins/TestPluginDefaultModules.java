/*
 * TestPluginDefaultModules.java
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

package com.publicuhc.pluginframework.testplugins;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.publicuhc.pluginframework.FrameworkJavaPlugin;
import com.publicuhc.pluginframework.locale.LocaleModule;
import com.publicuhc.pluginframework.locale.LocaleProvider;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mock;

public class TestPluginDefaultModules extends FrameworkJavaPlugin {

    public Plugin injectedPlugin;

    /**
     * This method is intended for unit testing purposes. Its existence may be temporary.
     *
     * @see org.bukkit.plugin.java.JavaPlugin
     */
    public TestPluginDefaultModules(PluginLoader loader, Server server, PluginDescriptionFile pdf, File file1, File file2)
    {
        super(loader, server, pdf, file1, file2);
    }

    @Override
    protected void initialModules(List<Module> modules)
    {
        List<Module> defaults = getDefaultModules();
        //mock out the reflection class to stop tests not finding the package
        Iterator<Module> iterator = defaults.iterator();
        Module toInsert = null;
        while(iterator.hasNext()) {
            Module module = iterator.next();
            if(module instanceof LocaleModule) {
                iterator.remove();
                toInsert = Modules.override(module).with(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(LocaleProvider.class).toInstance(mock(LocaleProvider.class));
                    }
                });
            }
        }
        modules.addAll(defaults);
        modules.add(toInsert);
    }

    @Inject
    public void setInjectedPlugin(Plugin plugin)
    {
        this.injectedPlugin = plugin;
    }
}
