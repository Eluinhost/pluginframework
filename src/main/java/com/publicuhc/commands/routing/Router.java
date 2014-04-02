package com.publicuhc.commands.routing;

import com.publicuhc.commands.CommandProxy;
import org.bukkit.command.Command;
import org.bukkit.command.TabExecutor;

import javax.annotation.Nullable;
import java.util.List;

public interface Router extends TabExecutor {

    /**
     * @return the route for the base command
     */
    @Nullable
    CommandProxy getForBaseCommand(String command);

    /**
     * @param command the command string
     * @return the commandproxy if found or null if not
     */
    @Nullable
    List<CommandProxy> getCommand(Command command, String parameters);

    /**
     * Register a class for commands, makes an instance
     * @param clazz the class to register commands for
     */
    void registerCommands(Class clazz);

    /**
     * @param object the object to register commands for
     */
    void registerCommands(Object object);
}
