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
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public abstract class FrameworkJavaPlugin extends JavaPlugin {

    private boolean m_useDefaultBindings = true;

    private Configurator m_configurator;
    private Translate m_translate;
    private Router m_router;

    public final void onEnable() {
        List<AbstractModule> modules = initialModules();
        if(modules == null) {
            modules = new ArrayList<AbstractModule>();
        }
        if(m_useDefaultBindings){
            modules.add(new CommandModule());
            modules.add(new ConfigurationModule());
            modules.add(new TranslateModule());
        }
        Injector injector = Guice.createInjector(modules);
        injector.injectMembers(this);
        onFrameworkEnable();
    }

    /**
     * Called when the framework is initialized
     */
    public void onFrameworkEnable() {
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
     * @param useDefaultBindings whether to use the defaults or not
     */
    public void initUseDefaultBindings(boolean useDefaultBindings) {
        m_useDefaultBindings = useDefaultBindings;
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
     * <b>ONLY USE THIS AFTER onEnable HAS BEEN CALLED. (onFrameworkEnable and later) otherwise it will return null</b>
     * @return the router object
     */
    public Router getRouter() {
        return m_router;
    }
    /**
     * <b>ONLY USE THIS AFTER onEnable HAS BEEN CALLED. (onFrameworkEnable and later) otherwise it will return null</b>
     * @return the configurator object
     */
    public Configurator getConfigurator() {
        return m_configurator;
    }

    /**
     * <b>ONLY USE THIS AFTER onEnable HAS BEEN CALLED. (onFrameworkEnable and later) otherwise it will return null</b>
     * @return the translate object
     */
    public Translate getTranslate() {
        return m_translate;
    }
}
