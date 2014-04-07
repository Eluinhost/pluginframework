package com.publicuhc.translate;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class TranslateModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Translate.class).to(DefaultTranslate.class);
        bind(String.class).annotatedWith(Names.named("base_locale_permission")).toInstance("translation.locale");
    }
}
