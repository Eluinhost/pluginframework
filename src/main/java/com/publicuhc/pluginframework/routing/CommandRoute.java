package com.publicuhc.pluginframework.routing;

import com.publicuhc.pluginframework.routing.exception.CommandInvocationException;
import com.publicuhc.pluginframework.routing.proxy.MethodProxy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.OptionException;

public interface CommandRoute
{

    /**
     * @return the option details that define the allowed options
     */
    public CommandOptionsParser getOptionDetails();

    /**
     * @return the proxy to run on command trigger
     */
    public MethodProxy getProxy();

    /**
     * @return the name of the command to run on
     */
    public String getCommandName();

    /**
     * Run this command route
     *
     * @param args the args for the method
     * @throws org.bukkit.craftbukkit.libs.joptsimple.OptionException                     when options do not match expected
     * @throws com.publicuhc.pluginframework.routing.exception.CommandInvocationException if exception thrown in command
     */
    public void run(Command command, CommandSender sender, String[] args) throws OptionException, CommandInvocationException;
}
