package com.publicuhc.configuration;

import com.google.inject.AbstractModule;

public class ConfigurationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Configurator.class).to(DefaultConfigurator.class);
    }
}
