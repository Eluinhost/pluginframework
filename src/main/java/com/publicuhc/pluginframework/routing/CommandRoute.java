package com.publicuhc.pluginframework.routing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.OptionException;
import org.bukkit.craftbukkit.libs.joptsimple.OptionParser;

public interface CommandRoute {

    /**
     * @return the option details that define the allowed options
     */
    public OptionParser getOptionDetails();

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
     * @param args the args for the method
     * @throws org.bukkit.craftbukkit.libs.joptsimple.OptionException when options do not match expected
     */
    public void run(Command command, CommandSender sender, String[] args) throws OptionException;
}