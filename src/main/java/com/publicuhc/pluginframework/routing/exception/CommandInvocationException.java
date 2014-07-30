package com.publicuhc.pluginframework.routing.exception;

public class CommandInvocationException extends Exception
{
    public CommandInvocationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CommandInvocationException(Throwable cause)
    {
        super("Exception thrown when running a command method", cause);
    }
}
