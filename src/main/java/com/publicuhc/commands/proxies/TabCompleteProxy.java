package com.publicuhc.commands.proxies;

import com.publicuhc.commands.requests.CommandRequest;

import java.lang.reflect.Method;
import java.util.List;

public class TabCompleteProxy extends AbstractMethodProxy {

    /**
     * @return the list of strings from triggering the tab complete
     */
    public List<String> trigger(CommandRequest request) throws ProxyTriggerException {
        Object instance = getInstance();
        Method method = getCommandMethod();
        try {
            return (List<String>) method.invoke(instance, request);
        } catch (Exception e) {
            throw new ProxyTriggerException(e);
        }
    }
}
