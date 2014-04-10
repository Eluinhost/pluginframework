package com.publicuhc.configuration;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public interface Configurator {

    /**
     * Get the config with the given name
     * Loads from the file id.yml in the data dir.
     * If file not found saves the file id.yml from the jar to the data dir
     * @param id the id to look for
     * @return the config, null if loading the config file failed
     */
    FileConfiguration getConfig(String id);

    /**
     * Save the config with the given id.
     * The config must already be loaded for this to work
     * @param id the id to save
     * @throws IOException - Thrown when the given file cannot be written to for any reason.
     */
    void saveConfig(String id) throws IOException;
}
