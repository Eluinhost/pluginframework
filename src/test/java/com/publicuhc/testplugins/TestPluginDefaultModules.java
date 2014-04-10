package com.publicuhc.testplugins;

import com.publicuhc.FrameworkJavaPlugin;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

import java.io.File;

public class TestPluginDefaultModules extends FrameworkJavaPlugin {
    /**
     * This method is intended for unit testing purposes. Its existence may be temporary.
     * @see org.bukkit.plugin.java.JavaPlugin
     */
    public TestPluginDefaultModules(PluginLoader loader, Server server, PluginDescriptionFile pdf, File file1, File file2) {
        super(loader, server, pdf, file1, file2);
    }
}
