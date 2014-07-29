package com.publicuhc.pluginframework.routing;

import com.publicuhc.pluginframework.commands.exceptions.CommandClassParseException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class RoutingMethodParser {

    /**
     * @param annotation the annotation to look for
     * @return true if found, false otherwise
     */
    public static <T extends Annotation> T getAnnotation(Method method, Class<T> annotation) {
        return method.getAnnotation(annotation);
    }

    /**
     * @param method the method to parse
     * @param instance the instance to use
     * @return the proxy built from the given method
     * @throws com.publicuhc.pluginframework.commands.exceptions.CommandClassParseException if errors were thrown during parse
     */
    public abstract CommandRoute parseCommandMethodAnnotation(Method method, Object instance) throws CommandClassParseException;

    /**
     * Check if the given method has the command method annotation
     * @param method the method to check
     */
    public abstract boolean hasCommandMethodAnnotation(Method method);
}