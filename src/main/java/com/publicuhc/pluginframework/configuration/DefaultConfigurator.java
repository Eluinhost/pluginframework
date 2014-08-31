/*
 * DefaultConfigurator.java
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
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.publicuhc.pluginframework.configuration.events.ConfigFileReloadedEvent;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class DefaultConfigurator implements Configurator {

    private final Map<String, FileConfiguration> m_configs = new HashMap<String, FileConfiguration>();
    private final File m_dataFolder;
    private final ClassLoader m_classLoader;

    @Inject
    protected DefaultConfigurator(@Named("dataFolder") File dataFolder, ClassLoader classLoader) {
        m_dataFolder = dataFolder;
        m_classLoader = classLoader;
    }

    @Override
    public Optional<FileConfiguration> getConfig(String id) {
        FileConfiguration config = m_configs.get(id);
        if (null == config) {
            return loadConfig(id);
        }
        return Optional.of(config);
    }

    @Override
    public void saveConfig(String id) {
        FileConfiguration configuration = m_configs.get(id);
        if (configuration != null) {
            try {
                configuration.save(m_dataFolder.getAbsoluteFile() + File.separator + id.replaceAll(":", Matcher.quoteReplacement(File.separator)) + ".yml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Optional<FileConfiguration> reloadConfig(String id) {
        Optional<FileConfiguration> config = loadConfig(id);

        if(!config.isPresent()) {
            return Optional.absent();
        }

        ConfigFileReloadedEvent event = new ConfigFileReloadedEvent(id, config.get());
        Bukkit.getPluginManager().callEvent(event);
        return config;
    }

    protected Optional<FileConfiguration> loadConfig(String id) {
        File customConfigFile = new File(m_dataFolder, id.replaceAll(":", Matcher.quoteReplacement(File.separator)) + ".yml");
        FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

        // Look for defaults in the jar
        Optional<InputStream> defConfigStream = getResource(id + ".yml");

        if(!defConfigStream.isPresent()) {
            return Optional.absent();
        }

        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream.get()));
        customConfig.options().copyDefaults(true);
        customConfig.setDefaults(defConfig);

        m_configs.put(id, customConfig);
        saveConfig(id);

        return Optional.of(customConfig);
    }

    /**
     * Load the resource from the plugin
     *
     * @param filename the filename to load
     * @return inputstream
     */
    protected Optional<InputStream> getResource(String filename) {
        Validate.notNull(filename, "Filename cannot be null");

        //getResource always uses /
        filename = filename.replaceAll(":", Matcher.quoteReplacement("/"));

        try {
            URL url = m_classLoader.getResource(filename);

            if (url == null) {
                return Optional.absent();
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return Optional.of(connection.getInputStream());
        } catch (IOException ex) {
            return Optional.absent();
        }
    }
}
