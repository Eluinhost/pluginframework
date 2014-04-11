package com.publicuhc.commands.routing.testcommands;

import com.publicuhc.commands.annotation.CommandMethod;
import com.publicuhc.commands.annotation.RouteInfo;
import com.publicuhc.commands.requests.CommandRequest;
import com.publicuhc.commands.requests.SenderType;
import com.publicuhc.commands.routing.BaseMethodRoute;
import com.publicuhc.commands.routing.MethodRoute;
import org.bukkit.Bukkit;

import java.util.regex.Pattern;

public class TestFullCommands {

    @CommandMethod
    public void banIP(CommandRequest request) {
        Bukkit.banIP(request.getArg(0));
        request.sendMessage("banned with message "+request.getMatcherResult().group(2));
    }

    @RouteInfo
    public MethodRoute banIPDetails() {
        return new BaseMethodRoute(
                Pattern.compile("^([\\d]{1,3}.[\\d]{1,3}.[\\d]{1,3}.[\\d]{1,3}) (.*)$"),
                new SenderType[]{SenderType.CONSOLE},
                "test.permission",
                "banIP"
        );
    }
}
