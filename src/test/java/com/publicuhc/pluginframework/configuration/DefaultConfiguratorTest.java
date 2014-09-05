/*
 * DefaultConfiguratorTest.java
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

import com.google.common.base.Optional;
import com.publicuhc.pluginframework.configuration.events.ConfigFileReloadedEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class DefaultConfiguratorTest {

    private DefaultConfigurator configurator;
    private File dataFolder;
    private PluginManager manager;

    @Test
    public void test_load_config() {
        Optional<FileConfiguration> configuration = configurator.reloadConfig("test");
        assertThat(configuration.isPresent()).isTrue();
        assertThat(configuration.get().getString("testString")).isEqualTo("teststring");

        configuration = configurator.reloadConfig("testfolder/subtest");
        assertThat(configuration.isPresent()).isTrue();
        assertThat(configuration.get().getInt("test1")).isEqualTo(20);
    }

    @Test
    public void test_save_config() {
        configurator.reloadConfig("test");
        configurator.saveConfig("test");
        File configFile = new File(dataFolder, "test.yml");
        assertThat(configFile.exists()).isTrue();

        configurator.reloadConfig("testfolder/subtest");
        configurator.saveConfig("testfolder/subtest");
        configFile = new File(dataFolder, "testfolder/subtest.yml");
        assertThat(configFile.exists()).isTrue();
    }

    @Test
    public void test_get_config() {
        Optional<FileConfiguration> configuration = configurator.getConfig("test");
        assertThat(configuration.get().getString("testString")).isEqualTo("teststring");

        configuration = configurator.getConfig("testfolder/subtest");
        assertThat(configuration.get().getInt("test1")).isEqualTo(20);
    }

    @Test
    public void test_reload_config() {
        Optional<FileConfiguration> configuration = configurator.getConfig("test");

        configurator.reloadConfig("test");

        assertThat(configurator.getConfig("test")).isNotSameAs(configuration);
        //should be called twice, 1 for initial load and another for the reload
        verify(manager, times(2)).callEvent(any(ConfigFileReloadedEvent.class));
    }

    @Before
    public void onSetUp() throws Exception {
        dataFolder = new File("target/testdatafolder");
        if (dataFolder.exists()) {
            deleteDirectory(dataFolder);
        }
        dataFolder.mkdir();
        configurator = new DefaultConfigurator(dataFolder, getClass().getClassLoader());
        mockStatic(Bukkit.class);
        manager = mock(PluginManager.class);
        when(Bukkit.getPluginManager()).thenReturn(manager);
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
