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
import com.publicuhc.pluginframework.util.YamlUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DefaultConfigurator implements Configurator {

    private final Map<String, FileConfiguration> m_configs = new HashMap<String, FileConfiguration>();
    private final File dataFolder;
    private final ClassLoader classLoader;

    @Inject
    protected DefaultConfigurator(@Named("dataFolder") File dataFolder, ClassLoader classLoader) {
        this.dataFolder = dataFolder;
        this.classLoader = classLoader;
    }

    @Override
    public Optional<FileConfiguration> getConfig(String id) {
        FileConfiguration config = m_configs.get(id);
        if (null == config) {
            return reloadConfig(id);
        }
        return Optional.of(config);
    }

    @Override
    public void saveConfig(String id) {
        FileConfiguration configuration = m_configs.get(id);
        if (configuration != null) {
            YamlUtil.saveConfiguration(configuration, dataFolder, id + ".yml");
        }
    }

    @Override
    public Optional<FileConfiguration> reloadConfig(String id)
    {
        try {
            Optional<YamlConfiguration> config = YamlUtil.loadYamlFromJAR(id + ".yml", classLoader);

            if(!config.isPresent()) {
                return Optional.absent();
            }

            ConfigFileReloadedEvent event = new ConfigFileReloadedEvent(id, config.get());
            Bukkit.getPluginManager().callEvent(event);

            YamlConfiguration configuration = config.get();
            m_configs.put(id, configuration);

            return Optional.of((FileConfiguration) configuration);
        } catch(Throwable e) {
            e.printStackTrace();
            return Optional.absent();
        }
    }


}
