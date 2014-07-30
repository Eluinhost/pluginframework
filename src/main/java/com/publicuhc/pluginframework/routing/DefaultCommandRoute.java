package com.publicuhc.pluginframework.routing;

import com.publicuhc.pluginframework.routing.exception.CommandInvocationException;
import com.publicuhc.pluginframework.routing.proxy.MethodProxy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.OptionException;
import org.bukkit.craftbukkit.libs.joptsimple.OptionParser;
import org.bukkit.craftbukkit.libs.joptsimple.OptionSet;

public class DefaultCommandRoute implements CommandRoute {

    private final MethodProxy proxy;
    private final OptionParser parser;
    private final String commandName;

    public DefaultCommandRoute(String commandName, MethodProxy proxy, OptionParser parser)
    {
        this.commandName = commandName;
        this.proxy = proxy;
        this.parser = parser;
    }

    @Override
    public OptionParser getOptionDetails()
    {
        return parser;
    }

    @Override
    public MethodProxy getProxy()
    {
        return proxy;
    }

    @Override
    public void run(Command command, CommandSender sender, String[] args) throws OptionException, CommandInvocationException
    {
        try {
            OptionSet optionSet = parser.parse(args);
            proxy.invoke(command, sender, optionSet);
        } catch (Throwable e) {
            throw new CommandInvocationException("Exception thrown when running the command " + command.getName(), e);
        }
    }

    @Override
    public String getCommandName()
    {
        return commandName;
    }
}
