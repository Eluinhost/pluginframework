/*
 * FrameworkJavaPluginTest.java
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

import com.google.inject.Module;
import com.publicuhc.pluginframework.testplugins.TestPluginDefaultModules;
import com.publicuhc.pluginframework.testplugins.TestPluginExtraModules;
import com.publicuhc.pluginframework.testplugins.TestPluginNoModules;
import com.publicuhc.pluginframework.testplugins.TestPluginReplaceModules;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FrameworkJavaPlugin.class, PluginDescriptionFile.class, JavaPluginLoader.class})
public class FrameworkJavaPluginTest {

    @Test
    public void test_no_modules() throws Exception
    {
        PluginLoader loader = mock(PluginLoader.class);
        Server server = mock(Server.class);
        PluginDescriptionFile pdf = mock(PluginDescriptionFile.class);
        File file1 = new File("target" + File.separator + "testdatafolder");

        PluginLogger logger = mock(PluginLogger.class);
        when(server.getLogger()).thenReturn(logger);

        TestPluginNoModules plugin = new TestPluginNoModules(loader, server, pdf, file1, file1);
        plugin.onLoad();
        plugin.onEnable();
    }

    @Test
    public void test_default_modules() throws Exception
    {
        PluginLoader loader = mock(PluginLoader.class);
        Server server = mock(Server.class);
        PluginDescriptionFile pdf = mock(PluginDescriptionFile.class);
        File file1 = new File("target" + File.separator + "testdatafolder");

        PluginLogger logger = mock(PluginLogger.class);
        when(server.getLogger()).thenReturn(logger);

        TestPluginDefaultModules plugin = new TestPluginDefaultModules(loader, server, pdf, file1, file1);
        assertThat(plugin.injectedPlugin).isNull();

        plugin.onLoad();
        assertThat(plugin.injectedPlugin).isNull();

        plugin.onEnable();
        assertThat(plugin.injectedPlugin).isSameAs(plugin);
    }

    @Test
    public void test_extra_modules() throws Exception
    {
        PluginLoader loader = mock(PluginLoader.class);
        Server server = mock(Server.class);
        PluginDescriptionFile pdf = mock(PluginDescriptionFile.class);
        File file1 = new File("target" + File.separator + "testdatafolder");

        PluginLogger logger = mock(PluginLogger.class);
        when(server.getLogger()).thenReturn(logger);

        TestPluginExtraModules plugin = new TestPluginExtraModules(loader, server, pdf, file1, file1);
        assertThat(plugin.i).isNull();

        plugin.onLoad();
        assertThat(plugin.i).isNull();

        plugin.onEnable();
        assertThat(plugin.i).isInstanceOf(TestPluginExtraModules.TestConcrete.class);
    }

    @Test
    public void test_override_modules() throws Exception
    {
        PluginLoader loader = mock(PluginLoader.class);
        Server server = mock(Server.class);
        PluginDescriptionFile pdf = mock(PluginDescriptionFile.class);
        File file1 = new File("target" + File.separator + "testdatafolder");

        PluginLogger logger = mock(PluginLogger.class);
        when(server.getLogger()).thenReturn(logger);

        TestPluginReplaceModules plugin = new TestPluginReplaceModules(loader, server, pdf, file1, file1);
        assertThat(plugin.logger).isNull();

        plugin.onLoad();
        assertThat(plugin.logger).isNull();

        plugin.onEnable();
        assertThat(plugin.logger).isInstanceOf(TestPluginReplaceModules.TestPluginLogger.class);
    }

    @Test(expected = IllegalStateException.class)
    public void test_wrong_classloader()
    {
        new FrameworkJavaPlugin() {
            @Override
            protected void initialModules(List<Module> modules)
            {}
        };
    }
}
