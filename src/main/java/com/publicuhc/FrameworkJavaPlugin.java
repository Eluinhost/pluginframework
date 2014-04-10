package com.publicuhc;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.publicuhc.commands.CommandModule;
import com.publicuhc.commands.routing.Router;
import com.publicuhc.configuration.Configurator;
import com.publicuhc.configuration.ConfigurationModule;
import com.publicuhc.translate.Translate;
import com.publicuhc.translate.TranslateModule;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class FrameworkJavaPlugin extends JavaPlugin {

    private Configurator m_configurator;
    private Translate m_translate;
    private Router m_router;

    /**
     * This method is intended for unit testing purposes. Its existence may be temporary.
     * @see org.bukkit.plugin.java.JavaPlugin
     */
    protected FrameworkJavaPlugin(PluginLoader loader, Server server, PluginDescriptionFile pdf, File file1, File file2) {
        super(loader, server, pdf, file1, file2);
    }

    @Override
    public final void onLoad() {
        List<AbstractModule> modules = initialModules();
        if(modules == null) {
            modules = new ArrayList<AbstractModule>();
        }
        final Plugin plugin = this;
        modules.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Plugin.class).toInstance(plugin);
            }
        });
        if(initUseDefaultBindings()){
            modules.add(new CommandModule());
            modules.add(new ConfigurationModule());
            modules.add(new TranslateModule());
        }
        Injector injector = Guice.createInjector(modules);
        injector.injectMembers(this);
        onFrameworkLoad();
    }

    /**
     * Called when the framework is loaded
     */
    public void onFrameworkLoad() {
    }

    /**
     * Return a list of extra modules to initialize DI with.
     * Override this method to change the extra modules to load with.
     * @return the list of modules
     */
    public List<AbstractModule> initialModules() {
        return null;
    }

    /**
     * If returns true will include default modules on init.
     * If false you must specify all the bindings for the framework
     * @return whether to use the defaults or not
     */
    public boolean initUseDefaultBindings() {
        return true;
    }

    /**
     * Set the router by injection
     * @param router the router object
     */
    @Inject
    private void setRouter(Router router) {
        m_router = router;
    }

    /**
     * Set the configurator by injection
     * @param configurator the configurator object
     */
    @Inject
    private void setConfigurator(Configurator configurator) {
        m_configurator = configurator;
    }

    /**
     * Set the translate by injection
     * @param translate the translate object
     */
    @Inject
    private void setTranslate(Translate translate) {
        m_translate = translate;
    }

    /**
     * <b>ONLY USE THIS AFTER onLoad HAS BEEN CALLED. (onFrameworkLoad and later) otherwise it will return null</b>
     * @return the router object
     */
    public Router getRouter() {
        return m_router;
    }
    /**
     * <b>ONLY USE THIS AFTER onLoad HAS BEEN CALLED. (onFrameworkLoad and later) otherwise it will return null</b>
     * @return the configurator object
     */
    public Configurator getConfigurator() {
        return m_configurator;
    }

    /**
     * <b>ONLY USE THIS AFTER onEnable HAS BEEN CALLED. (onFrameworkLoad and later) otherwise it will return null</b>
     * @return the translate object
     */
    public Translate getTranslate() {
        return m_translate;
    }
}
