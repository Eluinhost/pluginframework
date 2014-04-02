package com.publicuhc.commands.proxies;

import com.publicuhc.commands.requests.CommandRequest;

import java.lang.reflect.Method;

public class CommandProxy extends DefaultMethodProxy {

    /**
     * Trigger this command
     *
     * @param request the request params
     */
    public void trigger(CommandRequest request) throws ProxyTriggerException {
        Object instance = getInstance();
        Method method = getCommandMethod();
        try {
            method.invoke(instance, request);
        } catch (Exception e) {
            throw new ProxyTriggerException(e);
        }
    }
}
