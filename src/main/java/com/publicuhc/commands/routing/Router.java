package com.publicuhc.commands.routing;

import com.publicuhc.commands.proxies.CommandProxy;
import com.publicuhc.commands.proxies.TabCompleteProxy;
import org.bukkit.command.Command;
import org.bukkit.command.TabExecutor;

import javax.annotation.Nullable;
import java.util.List;

public interface Router extends TabExecutor {

    /**
     * @param command the command string
     * @param parameters the parameters
     * @return the commandproxy if found or null if not
     */
    @Nullable
    List<CommandProxy> getCommandProxy(Command command, String parameters);

    /**
     * @param command the command string
     * @param parameters the parameters
     * @return the tabcompleteproxy if found or null if not
     */
    List<TabCompleteProxy> getTabCompleteProxy(Command command, String parameters);

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
