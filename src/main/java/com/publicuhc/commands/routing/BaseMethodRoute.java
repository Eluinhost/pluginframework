package com.publicuhc.commands.routing;

import com.publicuhc.commands.requests.SenderType;

import java.util.regex.Pattern;

public class BaseMethodRoute implements MethodRoute {

    private final Pattern m_route;
    private final SenderType[] m_allowedTypes;
    private final String m_permission;
    private final String m_baseCommand;

    /**
     * @param route        the pattern to match to run
     * @param allowedTypes the allowed sender types for running
     * @param permission   the permission needed to run
     * @param baseCommand  the base command to run under
     */
    public BaseMethodRoute(Pattern route, SenderType[] allowedTypes, String permission, String baseCommand) {
        m_route = route;
        m_allowedTypes = allowedTypes;
        m_permission = permission;
        m_baseCommand = baseCommand;
    }

    @Override
    public Pattern getRoute() {
        return m_route;
    }

    @Override
    public SenderType[] getAllowedTypes() {
        return m_allowedTypes;
    }

    @Override
    public String getPermission() {
        return m_permission;
    }

    @Override
    public String getBaseCommand() {
        return m_baseCommand;
    }
}
