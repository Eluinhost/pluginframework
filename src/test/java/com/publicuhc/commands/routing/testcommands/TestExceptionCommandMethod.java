package com.publicuhc.commands.routing.testcommands;

import com.publicuhc.commands.annotation.CommandMethod;
import com.publicuhc.commands.annotation.RouteInfo;
import com.publicuhc.commands.requests.CommandRequest;
import com.publicuhc.commands.requests.SenderType;
import com.publicuhc.commands.routing.BaseMethodRoute;
import com.publicuhc.commands.routing.MethodRoute;

import java.util.regex.Pattern;

public class TestExceptionCommandMethod {

    @CommandMethod
    public void exception(CommandRequest request) {
        throw new IllegalAccessError();
    }

    @RouteInfo
    public MethodRoute exceptionDetails() {
        return new BaseMethodRoute(Pattern.compile(".*"), new SenderType[]{SenderType.CONSOLE}, "perm", "test");
    }
}
