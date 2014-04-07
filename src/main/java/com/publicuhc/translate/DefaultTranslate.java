package com.publicuhc.translate;

import com.google.inject.Inject;
import com.publicuhc.configuration.Config;

import java.util.Map;

public class DefaultTranslate implements Translate {

    private final Config m_config;

    @Inject
    protected DefaultTranslate(Config config){
        m_config = config;
    }

    @Override
    public String translate(String key) {
        return null;
    }

    @Override
    public String translate(String key, Map<String, String> vars) {
        return null;
    }

    @Override
    public String translate(String key, String var, String value) {
        return null;
    }
}
