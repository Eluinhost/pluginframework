package com.publicuhc.commands;

import com.publicuhc.commands.requests.CommandRequest;
import org.bukkit.command.Command;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public interface CommandProxy {

    /**
     * @param pattern the pattern to be matched to run
     */
    void setPattern(Pattern pattern);

    /**
     * @param method the method to run on command run
     */
    void setCommandMethod(Method method);

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
     * @return true if matches route, false otherwise
     */
    boolean doParamsMatch(String params);

    /**
     * Trigger this command
     * @param request the request params
     */
    void trigger(CommandRequest request);
}
