/*
 * FrameworkJavaPluginTest.java
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

package com.publicuhc.pluginframework;

import com.publicuhc.pluginframework.commands.routing.DefaultRouter;
import com.publicuhc.pluginframework.configuration.DefaultConfigurator;
import com.publicuhc.pluginframework.testplugins.TestPluginDefaultModules;
import com.publicuhc.pluginframework.testplugins.TestPluginExtraModules;
import com.publicuhc.pluginframework.testplugins.TestPluginReplaceModules;
import com.publicuhc.pluginframework.translate.DefaultTranslate;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FrameworkJavaPlugin.class, PluginDescriptionFile.class, JavaPluginLoader.class})
public class FrameworkJavaPluginTest {

    @Test
    public void testUseDefaultModules() throws Exception {
        PluginLoader loader = mock(PluginLoader.class);
        Server server = mock(Server.class);
        PluginDescriptionFile pdf = mock(PluginDescriptionFile.class);
        File file1 = new File("target" + File.separator + "testdatafolder");

        PluginLogger logger = mock(PluginLogger.class);
        when(server.getLogger()).thenReturn(logger);

        TestPluginDefaultModules plugin = new TestPluginDefaultModules(loader, server, pdf, file1, file1) { };

        assertThat(plugin.getConfigurator()).isNull();
        assertThat(plugin.getRouter()).isNull();
        assertThat(plugin.getTranslate()).isNull();

        plugin.onLoad();

        assertThat(plugin.getConfigurator()).isNull();
        assertThat(plugin.getRouter()).isNull();
        assertThat(plugin.getTranslate()).isNull();

        plugin.onEnable();

        assertThat(plugin.getConfigurator()).isInstanceOf(DefaultConfigurator.class);
        assertThat(plugin.getTranslate()).isInstanceOf(DefaultTranslate.class);
        assertThat(plugin.getRouter()).isInstanceOf(DefaultRouter.class);
    }

    @Test
    public void testUseExtraModules() throws Exception {
        PluginLoader loader = mock(PluginLoader.class);
        Server server = mock(Server.class);
        PluginDescriptionFile pdf = mock(PluginDescriptionFile.class);
        File file1 = new File("target" + File.separator + "testdatafolder");

        PluginLogger logger = mock(PluginLogger.class);
        when(server.getLogger()).thenReturn(logger);

        TestPluginExtraModules plugin = new TestPluginExtraModules(loader, server, pdf, file1, file1);

        assertThat(plugin.getConfigurator()).isNull();
        assertThat(plugin.getRouter()).isNull();
        assertThat(plugin.getTranslate()).isNull();

        assertThat(plugin.i).isNull();

        plugin.onLoad();

        assertThat(plugin.getConfigurator()).isNull();
        assertThat(plugin.getRouter()).isNull();
        assertThat(plugin.getTranslate()).isNull();

        assertThat(plugin.i).isNull();

        plugin.onEnable();

        assertThat(plugin.getConfigurator()).isInstanceOf(DefaultConfigurator.class);
        assertThat(plugin.getTranslate()).isInstanceOf(DefaultTranslate.class);
        assertThat(plugin.getRouter()).isInstanceOf(DefaultRouter.class);

        assertThat(plugin.i).isInstanceOf(TestPluginExtraModules.TestConcrete.class);
    }

    @Test
    public void testReplaceModules() throws Exception {
        PluginLoader loader = mock(PluginLoader.class);
        Server server = mock(Server.class);
        PluginDescriptionFile pdf = mock(PluginDescriptionFile.class);
        File file1 = new File("target" + File.separator + "testdatafolder");

        PluginLogger logger = mock(PluginLogger.class);
        when(server.getLogger()).thenReturn(logger);

        TestPluginReplaceModules plugin = new TestPluginReplaceModules(loader, server, pdf, file1, file1);

        assertThat(plugin.getConfigurator()).isNull();
        assertThat(plugin.getRouter()).isNull();
        assertThat(plugin.getTranslate()).isNull();
        assertThat(plugin.builder).isNull();

        plugin.onLoad();

        assertThat(plugin.getConfigurator()).isNull();
        assertThat(plugin.getRouter()).isNull();
        assertThat(plugin.getTranslate()).isNull();
        assertThat(plugin.builder).isNull();

        plugin.onEnable();

        assertThat(plugin.getConfigurator()).isInstanceOf(TestPluginReplaceModules.TestConcreteConfigurator.class);
        assertThat(plugin.getTranslate()).isInstanceOf(TestPluginReplaceModules.TestConcreteTranslate.class);
        assertThat(plugin.getRouter()).isInstanceOf(TestPluginReplaceModules.TestConcreteRouter.class);
        assertThat(plugin.builder).isInstanceOf(TestPluginReplaceModules.TestConcreteCommandRequestBuilder.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testWrongClassloader() {
        FrameworkJavaPlugin javaPlugin = new FrameworkJavaPlugin() { };
    }
}
