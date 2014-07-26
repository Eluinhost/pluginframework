package com.publicuhc.pluginframework.commands.routing;

import com.publicuhc.pluginframework.commands.exceptions.CommandClassParseException;

import java.lang.reflect.Method;

public interface RoutingMethodParser {

    /**
     * @param method the method to parse
     * @param instance the instance to use
     * @return the proxy built from the given method
     * @throws com.publicuhc.pluginframework.commands.exceptions.CommandClassParseException if errors were thrown during parse
     */
    public CommandRoute parseCommandMethodAnnotation(Method method, Object instance) throws CommandClassParseException;
}