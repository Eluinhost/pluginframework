package com.publicuhc.commands.routing.testcommands;

import com.publicuhc.commands.annotation.CommandMethod;
import com.publicuhc.commands.annotation.RouteInfo;
import com.publicuhc.commands.requests.CommandRequest;
import com.publicuhc.commands.requests.SenderType;
import com.publicuhc.commands.routing.BaseMethodRoute;
import com.publicuhc.commands.routing.MethodRoute;

import java.util.regex.Pattern;

public class TestMissingBaseCommands {

    @CommandMethod
    public void commandMissing(CommandRequest request) {

    }

    @RouteInfo
    public MethodRoute commandMissingDetails() {
        return new BaseMethodRoute(Pattern.compile("^arg$"), new SenderType[]{SenderType.CONSOLE}, "permission", "unknowncommand");
    }
}
