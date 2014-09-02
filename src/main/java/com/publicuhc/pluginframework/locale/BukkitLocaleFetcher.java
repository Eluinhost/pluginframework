package com.publicuhc.pluginframework.locale;

import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BukkitLocaleFetcher implements LocaleFetcher
{
    private Method playerHandleMethod;
    private Field localeField;

    @Inject
    @SuppressWarnings("unchecked")
    public BukkitLocaleFetcher(Plugin plugin)
    {
        // Get full package string of CraftServer.
        // org.bukkit.craftbukkit.version
        String packageName = plugin.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);

        String craftPlayerClassName = "org.bukkit.craftbukkit." + version + ".entity.CraftPlayer";

        try {
            Class craftPlayerClass = Class.forName(craftPlayerClassName);
            playerHandleMethod = craftPlayerClass.getMethod("getHandle");
            localeField = playerHandleMethod.getReturnType().getDeclaredField("locale");
            localeField.setAccessible(true);
        } catch(NoSuchMethodException e) {
            throw new UnknownDependencyException("Handle method not found");
        } catch(ClassNotFoundException e) {
            throw new UnknownDependencyException("CraftPlayer class not found");
        } catch(NoSuchFieldException e) {
            throw new UnknownDependencyException("Locale field not found");
        }
    }

    @Override
    public String getLocaleForPlayer(Player p)
    {
        String locale = "en_US";
        try {
            locale = (String) localeField.get(playerHandleMethod.invoke(p));
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        } catch(InvocationTargetException e) {
            e.printStackTrace();
        }
        return locale;
    }
}
