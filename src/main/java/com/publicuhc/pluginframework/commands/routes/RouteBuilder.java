/*
 * RouteBuilder.java
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

package com.publicuhc.pluginframework.commands.routes;

import com.publicuhc.pluginframework.commands.requests.SenderType;

import java.util.regex.Pattern;

public interface RouteBuilder {

    /**
     * Build into a route
     * @return the route
     */
    Route build();

    /**
     * Restrict by a command name
     * @param string the command name
     */
    RouteBuilder restrictCommand(String string);

    /**
     * Restrict to a specific permission
     * @param permission the permission
     */
    RouteBuilder restrictPermission(String permission);

    /**
     * Restrict to a certain pattern
     * @param expression the expression
     */
    RouteBuilder restrictPattern(Pattern expression);

    /**
     * Restrict to these sender types
     * @param types the senders to run for
     */
    RouteBuilder restrictSenderType(SenderType... types);

    /**
     * Only trigger command if number matched is <= this
     * @param matches the max amount
     */
    RouteBuilder maxMatches(int matches);

    /**
     * Restrict amount of arguments to within the limits
     * if either parameter is less than 0 it will be ignored
     * @param min the minimum
     * @param max the maximum
     * @return this
     */
    RouteBuilder restrictArgumentCount(int min, int max);

    /**
     * Restrict based on if the arguments start with the string, case insensitive
     * @param startsWith the string to check
     * @return this
     */
    RouteBuilder restrictStartsWith(String startsWith);

    /**
     * Reset the builder to build again
     * @return this
     */
    RouteBuilder reset();
}
