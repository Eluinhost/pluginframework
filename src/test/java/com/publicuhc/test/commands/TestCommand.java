package com.publicuhc.test.commands;

import com.publicuhc.commands.annotation.RouteInfo;
import com.publicuhc.commands.annotation.CommandMethod;
import com.publicuhc.commands.annotation.TabCompletion;
import com.publicuhc.commands.requests.CommandRequest;

public class TestCommand {

    @CommandMethod
    public void testCommand(CommandRequest request){
        //do stuff
    }

    @RouteInfo
    public void testCommandDetails(){

    }

    @TabCompletion
    public void testTabComplete(CommandRequest request){

    }

    @RouteInfo
    public void testTabCompleteDetails(){

    }
}
