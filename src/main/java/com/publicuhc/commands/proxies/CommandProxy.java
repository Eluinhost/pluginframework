package com.publicuhc.commands.proxies;

import com.publicuhc.commands.requests.CommandRequest;

public interface CommandProxy extends MethodProxy {

    /**
     * Trigger this command
     * @param request the request params
     */
    void trigger(CommandRequest request);
}
