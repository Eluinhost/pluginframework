/*
 * DefaultConfiguratorTest.java
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

package com.publicuhc.pluginframework.configuration;

import com.publicuhc.pluginframework.configuration.events.ConfigFileReloadedEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class DefaultConfiguratorTest {

    private DefaultConfigurator m_configurator;
    private File m_dataFolder;

    @Test
    public void testGetResource() {
        InputStream stream = m_configurator.getResource("test.yml");
        assertThat(stream, is(not(nullValue())));

        stream = m_configurator.getResource("nonexisting.yml");
        assertThat(stream, is(nullValue()));

        stream = m_configurator.getResource("testfolder:subtest.yml");
        assertThat(stream, is(not(nullValue())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullGetResource() {
        m_configurator.getResource(null);
    }

    @Test
    public void testLoadConfig() {
        FileConfiguration configuration = m_configurator.loadConfig("test");
        assertThat(configuration, is(not(nullValue())));
        assertThat(configuration.getString("testString"), is(equalTo("teststring")));

        configuration = m_configurator.loadConfig("testfolder:subtest");
        assertThat(configuration, is(not(nullValue())));
        assertThat(configuration.getInt("test1"), is(20));
    }

    @Test
    public void testSaveConfig() {
        m_configurator.loadConfig("test");
        m_configurator.saveConfig("test");
        File configFile = new File(m_dataFolder, "test.yml");
        assertTrue(configFile.exists());

        m_configurator.loadConfig("testfolder:subtest");
        m_configurator.saveConfig("testfolder:subtest");
        configFile = new File(m_dataFolder, "testfolder"+File.separator+"subtest.yml");
        assertTrue(configFile.exists());
    }

    @Test
    public void testGetConfig() {
        FileConfiguration configuration = m_configurator.getConfig("test");
        assertThat(configuration.getString("testString"), is(equalTo("teststring")));

        configuration = m_configurator.getConfig("testfolder:subtest");
        assertThat(configuration.getInt("test1"), is(20));
    }

    @Test
    public void testReloadConfig() {
        mockStatic(Bukkit.class);
        PluginManager manager = mock(PluginManager.class);
        when(Bukkit.getPluginManager()).thenReturn(manager);

        FileConfiguration configuration = m_configurator.getConfig("test");

        m_configurator.reloadConfig("test");

        assertThat(m_configurator.getConfig("test"), is(not(sameInstance(configuration))));
        verify(manager, times(1)).callEvent(any(ConfigFileReloadedEvent.class));
    }

    @Before
    public void onSetUp() throws Exception {
        m_dataFolder = new File("target"+File.separator+"testdatafolder");
        if(m_dataFolder.exists()){
            deleteDirectory(m_dataFolder);
        }
        m_dataFolder.mkdir();
        m_configurator = new DefaultConfigurator(m_dataFolder);
    }

    public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return (path.delete());
    }
}
