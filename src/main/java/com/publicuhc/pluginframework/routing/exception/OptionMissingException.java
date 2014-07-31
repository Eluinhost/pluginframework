package com.publicuhc.pluginframework.routing.exception;

import org.bukkit.craftbukkit.libs.joptsimple.OptionException;

import java.util.Collection;

public class OptionMissingException extends OptionException
{
    public OptionMissingException(Collection<String> options)
    {
        super(options);
    }

    public OptionMissingException(Collection<String> options, Throwable cause)
    {
        super(options, cause);
    }
}
