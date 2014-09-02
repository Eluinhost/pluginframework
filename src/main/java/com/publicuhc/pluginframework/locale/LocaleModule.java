package com.publicuhc.pluginframework.locale;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.util.Locale;

public class LocaleModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(LocaleProvider.class).to(BukkitLocaleProvider.class);
        bind(LocaleFetcher.class).to(BukkitLocaleFetcher.class);
        bind(Locale.class).annotatedWith(Names.named("defaultLocale")).toInstance(Locale.ENGLISH);
    }
}
