package com.publicuhc.commands.routing.testcommands;

import com.publicuhc.commands.annotation.CommandMethod;
import com.publicuhc.commands.annotation.RouteInfo;
import com.publicuhc.commands.annotation.TabCompletion;
import com.publicuhc.commands.requests.CommandRequest;
import com.publicuhc.commands.requests.SenderType;
import com.publicuhc.commands.routing.BaseMethodRoute;
import com.publicuhc.commands.routing.MethodRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.powermock.api.mockito.PowerMockito.mock;

public class TestValidCommands {

    @CommandMethod
    public void testCommand(CommandRequest request) {}

    @TabCompletion
    public List<String> testTabComplete(CommandRequest request) {
        return new ArrayList<String>();
    }

    @RouteInfo
    public MethodRoute testCommandDetails() {
        return new BaseMethodRoute(Pattern.compile("^arg$"), new SenderType[]{SenderType.CONSOLE, SenderType.REMOTE_CONSOLE}, "test.permission", "test");
    }

    @RouteInfo
    public MethodRoute testTabCompleteDetails() {
        return new BaseMethodRoute(Pattern.compile("^arg$"), new SenderType[]{SenderType.CONSOLE, SenderType.REMOTE_CONSOLE}, "test.permission", "test");
    }
}
