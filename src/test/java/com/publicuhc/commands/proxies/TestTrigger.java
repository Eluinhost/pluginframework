package com.publicuhc.commands.proxies;

import com.publicuhc.commands.requests.CommandRequest;

import java.util.Arrays;
import java.util.List;

public class TestTrigger {

    public static final List<String> args = Arrays.asList("a", "b", "c");

    public void triggerCommandMethod(CommandRequest request){
    }
    public List<String> triggerTabComplete(CommandRequest request){
        return  args;
    }
}
