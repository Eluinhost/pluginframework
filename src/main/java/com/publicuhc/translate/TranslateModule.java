package com.publicuhc.translate;

import com.google.inject.AbstractModule;

public class TranslateModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Translate.class).to(DefaultTranslate.class);
    }
}
