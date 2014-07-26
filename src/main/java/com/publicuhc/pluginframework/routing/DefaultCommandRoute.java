package com.publicuhc.pluginframework.routing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.OptionParser;
import org.bukkit.craftbukkit.libs.joptsimple.OptionSet;

import java.lang.reflect.InvocationTargetException;

public class DefaultCommandRoute implements CommandRoute {

    private MethodProxy proxy;
    private OptionParser parser;

    public DefaultCommandRoute(MethodProxy proxy, OptionParser parser)
    {
        this.proxy = proxy;
        this.parser = parser;
    }

    @Override
    public OptionParser getOptionDetails()
    {
        return parser;
    }

    @Override
    public void setOptionDetails(OptionParser options)
    {
        parser = options;
    }

    @Override
    public void setProxy(MethodProxy proxy)
    {
        this.proxy = proxy;
    }

    @Override
    public MethodProxy getProxy()
    {
        return proxy;
    }

    @Override
    public void run(Command command, CommandSender sender, OptionSet args)
    {
        try {
            proxy.invoke(command, sender, args);

            //TODO catch the exceptions and throw it wrapped for whatever runs this to catch
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
