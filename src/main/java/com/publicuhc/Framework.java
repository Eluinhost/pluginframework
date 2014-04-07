package com.publicuhc;

import com.google.inject.*;
import com.publicuhc.commands.routing.Router;
import com.publicuhc.configuration.Config;
import com.publicuhc.translate.Translate;

@Singleton
public class Framework {

    private Router m_router;
    private Translate m_translate;
    private Config m_config;

    @Inject
    private Framework(Router router, Config config, Translate translate) {
        m_router = router;
        m_translate = translate;
        m_config = config;
    }

    public Router getRouter() {
        return m_router;
    }

    public Config getConfig() { return m_config; }

    public Translate getTranslate() { return m_translate; }
}
