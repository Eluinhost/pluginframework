/*
 * DefaultTranslate.java
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

package com.publicuhc.pluginframework.translate;

import com.google.inject.Inject;
import com.publicuhc.pluginframework.locale.LocaleProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class DefaultTranslate implements Translate {

    private final LocaleProvider locales;
    private final YamlControl controller;
    private final ClassLoader loader;

    @Inject
    protected DefaultTranslate(LocaleProvider localeProvider, YamlControl controller, PluginLogger logger, Plugin plugin)
    {
        this.locales = localeProvider;
        this.controller = controller;
        this.loader = plugin.getClass().getClassLoader();
    }

    protected ResourceBundle getConfigForLocale(Locale locale)
    {
        return YamlResourceBundle.getBundle("translations.lang", locale, loader, controller);
    }

    @Override
    public String translate(String key, CommandSender sender, Object... params)
    {
        return translate(key, locales.localeForCommandSender(sender), params);
    }

    @Override
    public void sendMessage(String key, CommandSender sender, Object... params)
    {
        sender.sendMessage(translate(key, sender, params));
    }

    @Override
    public void broadcastMessage(String key, Object... params)
    {
        broadcastMessageForPermission("", key, params);
    }

    @Override
    public void broadcastMessageForPermission(String permission, String key, Object... params)
    {
        boolean checkPermissions = !permission.isEmpty();
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(checkPermissions && player.hasPermission(permission)) {
                sendMessage(key, player, params);
            }
        }
    }

    @Override
    public String translate(String key, Locale locale, Object... params)
    {
        ResourceBundle configuration = getConfigForLocale(locale);

        String value = null;
        try {
            value = configuration.getString(key);
        }
        catch (MissingResourceException ignored){}

        if (null == value) {
            value = key;
        }

        value = String.format(locale, value, params);
        value = ChatColor.translateAlternateColorCodes('&', value);

        return value;
    }
}
