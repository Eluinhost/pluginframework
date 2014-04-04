package com.publicuhc;

import com.google.inject.*;
import com.publicuhc.commands.routing.Router;

@Singleton
public class Framework {

    private Router m_router;

    @Inject
    private Framework(Router router) {
        m_router = router;
    }

    public Router getRouter() {
        return m_router;
    }
}
