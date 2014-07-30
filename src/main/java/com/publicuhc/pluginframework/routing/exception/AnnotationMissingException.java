package com.publicuhc.pluginframework.routing.exception;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationMissingException extends CommandParseException
{
    public AnnotationMissingException(Method method, Class<? extends Annotation> annotation)
    {
        super("Annotation @" + annotation.getSimpleName() + " is missing from the method " + method.getName());
    }
}
