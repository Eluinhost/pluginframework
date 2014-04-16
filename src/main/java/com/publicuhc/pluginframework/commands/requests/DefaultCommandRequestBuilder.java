/*
 * DefaultCommandRequestBuilder.java
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

package com.publicuhc.pluginframework.commands.requests;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;

public class DefaultCommandRequestBuilder implements CommandRequestBuilder {

    private Command m_command;
    private List<String> m_arguments;
    private CommandSender m_commandSender;
    private MatchResult m_matchResult;

    @Override
    public CommandRequest build() {
        if (!isValid()) {
            throw new IllegalStateException("Command request state is invalid");
        }
        return new CommandRequest(m_command, m_arguments, m_commandSender, m_matchResult);
    }

    @Override
    @Nonnull
    public CommandRequestBuilder setCommand(Command command) {
        if (null == command) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        m_command = command;
        return this;
    }

    protected Command getCommand() {
        return m_command;
    }

    @Override
    public CommandRequestBuilder setMatchResult(MatchResult result) {
        if (null == result) {
            throw new IllegalArgumentException("Match result cannot be null");
        }
        m_matchResult = result;
        return this;
    }

    protected MatchResult getMatchResult() {
        return m_matchResult;
    }

    @Override
    public CommandRequestBuilder setArguments(List<String> arguments) {
        if (null == arguments) {
            throw new IllegalArgumentException("Argument list cannot be null");
        }
        m_arguments = arguments;
        return this;
    }

    protected List<String> getArguments() {
        return m_arguments;
    }

    @Override
    public CommandRequestBuilder setArguments(String[] arguments) {
        if (null == arguments) {
            throw new IllegalArgumentException("Argument list cannot be null");
        }
        return setArguments(Arrays.asList(arguments));
    }

    @Override
    public CommandRequestBuilder setSender(CommandSender sender) {
        if (null == sender) {
            throw new IllegalArgumentException("Sender cannot be null");
        }
        m_commandSender = sender;
        return this;
    }

    protected CommandSender getSender() {
        return m_commandSender;
    }

    @Override
    public boolean isValid() {
        return !(m_commandSender == null || m_command == null || m_arguments == null || m_matchResult == null);
    }

    @Override
    public void clear() {
        m_command = null;
        m_commandSender = null;
        m_arguments = null;
        m_matchResult = null;
    }
}
