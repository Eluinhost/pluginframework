package com.publicuhc.pluginframework.routing.exception;

public class CommandParseException extends Exception
{
    public CommandParseException(String message)
    {
        super(message);
    }

    public CommandParseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CommandParseException(Throwable cause)
    {
        super("Failed to parse a command method", cause);
    }
}
