package com.publicuhc.commands.proxies;

import com.publicuhc.commands.requests.CommandRequest;

import java.util.List;

public interface TabCompleteProxy extends MethodProxy {

    /**
     * @return the list of strings from triggering the tab complete
     */
    List<String> trigger(CommandRequest request);
}
