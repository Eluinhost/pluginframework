package com.publicuhc.pluginframework.commands.routes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class RouteMatcher {

    private final Route m_route;

    public RouteMatcher(Route route) {
        m_route = route;
    }

    public boolean matches(CommandSender sender, Command command, String args) {
        Route currentRoute = m_route;
        while(currentRoute.matches(sender, command, args)) {
            currentRoute = currentRoute.getNextChain();
        }
    }
}
