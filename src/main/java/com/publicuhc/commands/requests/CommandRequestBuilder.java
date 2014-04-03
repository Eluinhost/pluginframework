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
