package com.publicuhc.commands.proxies;

import com.publicuhc.commands.requests.CommandRequest;

import java.util.List;

public class TabCompleteProxy extends AbstractMethodProxy {

    /**
     * @return the list of strings from triggering the tab complete
     */
    public List<String> trigger(CommandRequest request){
        return null;//TODO trigger the method
    }
}
