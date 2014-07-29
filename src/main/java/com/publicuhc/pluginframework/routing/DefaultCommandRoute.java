package com.publicuhc.pluginframework.routing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.OptionException;
import org.bukkit.craftbukkit.libs.joptsimple.OptionParser;
import org.bukkit.craftbukkit.libs.joptsimple.OptionSet;

import java.lang.reflect.InvocationTargetException;

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
    public void run(Command command, CommandSender sender, String[] args) throws OptionException
    {
        OptionSet optionSet = parser.parse(args);

        try {
            proxy.invoke(command, sender, optionSet);
            //TODO catch the exceptions and throw it wrapped for whatever runs this to catch
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCommandName()
    {
        return commandName;
    }
}
