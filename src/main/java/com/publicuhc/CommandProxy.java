package com.publicuhc;

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
}
