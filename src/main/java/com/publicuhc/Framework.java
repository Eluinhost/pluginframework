package com.publicuhc;

import com.google.inject.Inject;
import com.publicuhc.commands.routing.Router;

public class Framework {

    private final Router m_router;

    @Inject
    private Framework(Router router){
        m_router = router;
    }

    public Router getRouter(){
        return m_router;
    }
}
