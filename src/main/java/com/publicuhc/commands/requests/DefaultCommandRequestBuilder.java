package com.publicuhc.commands.requests;

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

    @Override
    public CommandRequestBuilder setMatchResult(MatchResult result) {
        m_matchResult = result;
        return this;
    }

    @Override
    public CommandRequestBuilder setArguments(List<String> arguments) {
        if (null == arguments) {
            throw new IllegalArgumentException("Argument list cannot be null");
        }
        m_arguments = arguments;
        return this;
    }

    @Override
    public CommandRequestBuilder setArguments(String[] arguments) {
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

    @Override
    public boolean isValid() {
        return !(m_commandSender == null || m_command == null || m_arguments == null);
    }

    @Override
    public void clear() {
        m_command = null;
        m_commandSender = null;
        m_arguments = null;
    }
}
