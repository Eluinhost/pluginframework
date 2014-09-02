package com.publicuhc.pluginframework.locale;

import com.google.inject.AbstractModule;

public class LocaleModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(LocaleProvider.class).to(BukkitLocaleProvider.class);
        bind(LocaleFetcher.class).to(BukkitLocaleFetcher.class);
    }
}
