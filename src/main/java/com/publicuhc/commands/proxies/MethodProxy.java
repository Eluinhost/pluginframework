package com.publicuhc.commands.proxies;

import org.bukkit.command.Command;

import java.lang.reflect.Method;
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
     * @return true if matches route, false otherwise
     */
    boolean doParamsMatch(String params);
}
