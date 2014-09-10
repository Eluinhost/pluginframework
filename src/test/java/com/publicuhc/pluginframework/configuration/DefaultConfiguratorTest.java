/*
 * DefaultConfiguratorTest.java
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
