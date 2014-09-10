/*
 * BukkitLocaleFetcher.java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
