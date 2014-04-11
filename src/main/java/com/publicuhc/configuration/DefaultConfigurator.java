package com.publicuhc.configuration;

import com.google.inject.Inject;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DefaultConfigurator implements Configurator {

    private final Plugin m_plugin;
    private final Map<String, FileConfiguration> m_configs = new HashMap<String, FileConfiguration>();

    @Inject
    public DefaultConfigurator(Plugin plugin) {
        m_plugin = plugin;
    }

    @Override
    public FileConfiguration getConfig(String id) {
        FileConfiguration config = m_configs.get(id);
        if (config == null) {
            try {
                config = loadFromFile(id + ".yml", true);
                m_configs.put(id, config);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return config;
    }

    @Override
    public void saveConfig(String id) throws IOException {
        FileConfiguration configuration = m_configs.get(id);
        if (configuration != null) {
            configuration.save(id + ".yml");
        }
    }

    /**
     * Load the config from the data directory and save it
     *
     * @param path        the path to load
     * @param setDefaults whether to set default values or not
     * @return the saved config
     * @throws IllegalArgumentException - if saving from the JAR failed after failed load from the data dir
     * @throws java.io.IOException      - if failed to save the config after options
     */
    protected FileConfiguration loadFromFile(String path, boolean setDefaults) throws IOException {
        File configFile = new File(m_plugin.getDataFolder(), path);
        if (!configFile.exists()) {
            saveFromJar(path, path, false);
            configFile = new File(m_plugin.getDataFolder(), path);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        if (setDefaults) {
            // Look for defaults in the jar
            InputStream defConfigStream = m_plugin.getResource(path);
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                config.setDefaults(defConfig);
            }
            config.save(path);
        }
        return config;
    }

    /**
     * Save the file from the jar to the data directory
     * The resource is saved into the plugin's data folder using the same hierarchy as the .jar file (subdirectories are preserved).
     *
     * @param replace  if true, the embedded resource will overwrite the contents of an existing file.
     * @param path     the file in the jar
     * @param filename the file name to save as
     * @throws IllegalArgumentException - if the resource path is null, empty, or points to a nonexistent resource.
     */
    protected void saveFromJar(String path, String filename, boolean replace) {
        m_plugin.saveResource(path, false);
    }
}
