package com.publicuhc.pluginframework.routing.exception;

public class CommandInvocationException extends Exception
{
    private final Throwable wrappedException;

    /**
     * Creates a new exception caused during a command invocation
     * @param exception the cause
     */
    public CommandInvocationException(Throwable exception)
    {
        wrappedException = exception;
    }

    /**
     * @return the original cause of the exception
     */
    public Throwable getWrappedException()
    {
        return wrappedException;
    }
}
