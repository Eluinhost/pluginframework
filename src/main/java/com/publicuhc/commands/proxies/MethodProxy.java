/*
 * MethodProxy.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of PluginFramework.
 *
 * PluginFramework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PluginFramework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PluginFramework.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.commands.proxies;

import com.publicuhc.commands.requests.SenderType;
import org.bukkit.command.Command;

import java.lang.reflect.Method;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public interface MethodProxy {

    /**
     * @param pattern the pattern to be matched to run
     */
    void setPattern(Pattern pattern);

    /**
     * @return the pattern to be matched on
     */
    Pattern getPattern();

    /**
     * @param object the instance to run against
     */
    void setInstance(Object object);

    /**
     * @return the instance to run with
     */
    Object getInstance();

    /**
     * @param method the method to run on command run
     */
    void setCommandMethod(Method method);

    /**
     * @return the method to run the command on
     */
    Method getCommandMethod();

    /**
     * @param command the base command
     */
    void setBaseCommand(Command command);

    /**
     * @return the base command
     */
    Command getBaseCommand();

    /**
     * @param params the parameters to check
     * @return the match result or null if no match
     */
    MatchResult paramsMatch(String params);

    /**
     * @return the permission, null if any allowed
     */
    String getPermission();

    /**
     * @param permission the permission to use
     */
    void setPermission(String permission);

    /**
     * @return the allowed senders
     */
    SenderType[] getAllowedSenders();

    /**
     * @param allowedSenders the allowed senders
     */
    void setAllowedSenders(SenderType[] allowedSenders);
}
