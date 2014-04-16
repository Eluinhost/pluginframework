/*
 * DefaultConfigurator.java
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

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.publicuhc.configuration.events.ConfigFileReloadedEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class DefaultConfigurator implements Configurator {

    private final Map<String, FileConfiguration> m_configs = new HashMap<String, FileConfiguration>();
    private final File m_dataFolder;

    @Inject
    protected DefaultConfigurator(@Named("dataFolder") File dataFolder) {
        m_dataFolder = dataFolder;
    }

    @Override
    public FileConfiguration getConfig(String id) {
        FileConfiguration config = m_configs.get(id);
        if (null == config) {
            config = loadConfig(id);
        }
        return config;
    }

    @Override
    public void saveConfig(String id) {
        FileConfiguration configuration = m_configs.get(id);
        if (configuration != null) {
            try {
                configuration.save(m_dataFolder.getAbsoluteFile()+"/"+id + ".yml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public FileConfiguration reloadConfig(String id) {
        FileConfiguration config = loadConfig(id);
        ConfigFileReloadedEvent event = new ConfigFileReloadedEvent(id, config);
        Bukkit.getPluginManager().callEvent(event);
        return config;
    }

    protected FileConfiguration loadConfig(String id) {
        File customConfigFile = new File(m_dataFolder, id+".yml");
        FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

        // Look for defaults in the jar
        InputStream defConfigStream = getResource(id + ".yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            customConfig.setDefaults(defConfig);
            customConfig.options().copyDefaults(true);
        }
        m_configs.put(id, customConfig);
        return customConfig;
    }

    /**
     * Load the resource from the plugin
     * @param filename the filename to load
     * @return inputstream
     */
    protected InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = this.getClass().getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }
}
