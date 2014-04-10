package com.publicuhc;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestPluginExtraModules extends FrameworkJavaPlugin {

    /**
     * This method is intended for unit testing purposes. Its existence may be temporary.
     * @see org.bukkit.plugin.java.JavaPlugin
     */
    protected TestPluginExtraModules(PluginLoader loader, Server server, PluginDescriptionFile pdf, File file1, File file2) {
        super(loader, server, pdf, file1, file2);
    }

    public TestInterface i;
    @Inject
    public void setTest(TestInterface i) {
        this.i = i;
    }
    @Override
    public List<AbstractModule> initialModules() {
        List<AbstractModule> modules = new ArrayList<AbstractModule>();
        AbstractModule module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(TestInterface.class).to(TestConcrete.class);
            }
        };
        modules.add(module);
        return modules;
    }

    public static interface TestInterface {}
    public static class TestConcrete implements TestInterface {}
}
