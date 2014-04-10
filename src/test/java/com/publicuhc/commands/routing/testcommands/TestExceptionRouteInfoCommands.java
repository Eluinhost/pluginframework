package com.publicuhc.commands.routing.testcommands;

import com.publicuhc.commands.annotation.CommandMethod;
import com.publicuhc.commands.annotation.RouteInfo;
import com.publicuhc.commands.requests.CommandRequest;
import com.publicuhc.commands.routing.MethodRoute;

import javax.naming.OperationNotSupportedException;

public class TestExceptionRouteInfoCommands {

    @CommandMethod
    public void command(CommandRequest request){

    }

    @RouteInfo
    public MethodRoute commandDetails() {
        throw new IllegalAccessError();
    }
}
