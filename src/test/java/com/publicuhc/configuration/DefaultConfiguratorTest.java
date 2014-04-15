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

package com.publicuhc.configuration;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.mock;


@RunWith(PowerMockRunner.class)
public class DefaultConfiguratorTest {

    private DefaultConfigurator m_configurator;
    private Plugin m_plugin;
    private File m_dataFolder;

    @Before
    public void onSetUp() throws Exception {
        m_plugin = mock(JavaPlugin.class);
        m_dataFolder = new File("target/testdatafolder");
        if(m_dataFolder.exists()){
            deleteDirectory(m_dataFolder);
        }
        m_dataFolder.mkdir();
        //when(m_plugin.getDataFolder()).thenReturn(m_dataFolder); //TODO stackoverflow error for overriding this final method because equals is final too...
        doCallRealMethod().when(m_plugin).saveResource("test.yml", false);
        m_configurator = new DefaultConfigurator(m_plugin);

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
