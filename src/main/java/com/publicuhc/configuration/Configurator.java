/*
 * Configurator.java
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

import org.bukkit.configuration.file.FileConfiguration;

public interface Configurator {

    /**
     * Get the config with the given name
     * Loads from the file id.yml in the data dir.
     * If file not found saves the file id.yml from the jar to the data dir
     *
     * @param id the id to look for
     * @return the config, null if loading the config file failed
     */
    FileConfiguration getConfig(String id);

    /**
     * Save the config with the given id.
     * The config must already be loaded for this to work
     *
     * @param id the id to save
     */
    void saveConfig(String id);

    /**
     * Reload the given config
     * @param id the id to look for
     */
    FileConfiguration reloadConfig(String id);
}
