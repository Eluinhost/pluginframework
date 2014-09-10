/*
 * DefaultConfigurator.java
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
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.publicuhc.pluginframework.configuration.events.ConfigFileReloadedEvent;
import com.publicuhc.pluginframework.util.YamlUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

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
            Optional<FileConfiguration> config = YamlUtil.loadConfigWithDefaults(id + ".yml", classLoader, dataFolder);

            if(!config.isPresent()) {
                return Optional.absent();
            }

            ConfigFileReloadedEvent event = new ConfigFileReloadedEvent(id, config.get());
            Bukkit.getPluginManager().callEvent(event);

            FileConfiguration configuration = config.get();
            m_configs.put(id, configuration);

            return Optional.of(configuration);
        } catch(Throwable e) {
            e.printStackTrace();
            return Optional.absent();
        }
    }


}
