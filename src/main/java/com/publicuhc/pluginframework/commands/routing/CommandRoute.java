package com.publicuhc.pluginframework.commands.routing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.OptionParser;
import org.bukkit.craftbukkit.libs.joptsimple.OptionSet;

public interface CommandRoute {

    /**
     * @return the option details that define the allowed options
     */
    public OptionParser getOptionDetails();

    /**
     * @param options the option details that define the allowed options
     */
    public void setOptionDetails(OptionParser options);

    /**
     * @param proxy the proxy to run on command trigger
     */
    public void setProxy(MethodProxy proxy);

    /**
     * @return the proxy to run on command trigger
     */
    public MethodProxy getProxy();

    /**
     * Run this command route
     * @param args the args for the method
     */
    public void run(Command command, CommandSender sender, OptionSet args);
}
