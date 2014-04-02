package com.publicuhc.commands.requests;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

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
     * @return whether the state is valid or not
     */
    boolean isValid();

    /**
     * Clear ready for building again
     */
    void clear();
}
