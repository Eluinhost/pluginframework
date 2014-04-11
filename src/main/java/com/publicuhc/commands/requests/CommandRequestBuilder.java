/*
 * CommandRequestBuilder.java
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

package com.publicuhc.commands.requests;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.regex.MatchResult;

public interface CommandRequestBuilder {

    /**
     * @return build a new request from the parameters
     */
    CommandRequest build();

    /**
     * @param command the command ran
     * @return this
     */
    CommandRequestBuilder setCommand(Command command);

    /**
     * @param arguments the argument list
     * @return this
     */
    CommandRequestBuilder setArguments(List<String> arguments);

    /**
     * @param arguments the argument list
     * @return this
     */
    CommandRequestBuilder setArguments(String[] arguments);

    /**
     * @param sender the command sender
     * @return this
     */
    CommandRequestBuilder setSender(CommandSender sender);

    /**
     * @param result the match results
     * @return this
     */
    CommandRequestBuilder setMatchResult(MatchResult result);

    /**
     * @return whether the state is valid or not
     */
    boolean isValid();

    /**
     * Clear ready for building again
     */
    void clear();
}
