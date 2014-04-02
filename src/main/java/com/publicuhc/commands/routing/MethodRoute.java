package com.publicuhc.commands.routing;

import com.publicuhc.commands.requests.SenderType;

import java.util.regex.Pattern;

public interface MethodRoute {

    /**
     * @return the pattern to match to run
     */
    Pattern getRoute();

    /**
     * @return the allowed senders for this
     */
    SenderType[] getAllowedTypes();

    /**
     * @return the permission needed
     */
    String getPermission();

    /**
     * @return the base command to run on
     */
    String getBaseCommand();
}
