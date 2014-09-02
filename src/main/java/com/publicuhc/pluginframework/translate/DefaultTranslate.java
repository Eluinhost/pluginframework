/*
 * DefaultTranslate.java
 *
 * Copyright (c) 2014. Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of PluginFramework.
 *
 * PluginFramework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PluginFramework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PluginFramework.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.pluginframework.translate;

import com.google.inject.Inject;
import com.publicuhc.pluginframework.configuration.Configurator;
import org.apache.commons.lang.LocaleUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class DefaultTranslate implements Translate {

    private final TranslateReflection locales;
    private final YamlControl controller;
    private final ClassLoader loader;

    private Locale commandBlockLocale = Locale.ENGLISH;
    private Locale remoteConsoleLocale = Locale.ENGLISH;
    private Locale consoleLocale = Locale.ENGLISH;

    @Inject
    protected DefaultTranslate(TranslateReflection locales, YamlControl controller, PluginLogger logger, Plugin plugin)
    {
        this.locales = locales;
        this.controller = controller;
        this.loader = plugin.getClass().getClassLoader();
    }

    @Inject(optional = true)
    protected void setConfigurator(Configurator configurator)
    {
        FileConfiguration config = configurator.getConfig("locales");

        commandBlockLocale = LocaleUtils.toLocale(config.getString("commandBlock", "en_US"));
        remoteConsoleLocale = LocaleUtils.toLocale(config.getString("remoteConsole", "en_US"));
        consoleLocale = LocaleUtils.toLocale(config.getString("console", "en_US"));
    }

    protected ResourceBundle getConfigForLocale(Locale locale)
    {
        return YamlResourceBundle.getBundle("translations.lang", locale, loader, controller);
    }

    @Override
    public String translate(String key, CommandSender sender, Object... params)
    {
        return translate(key, getLocaleForSender(sender), params);
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

    @Override
    public Locale getLocaleForSender(CommandSender sender)
    {
        if(sender instanceof BlockCommandSender)
            return commandBlockLocale;

        if(sender instanceof ConsoleCommandSender)
            return consoleLocale;

        if(sender instanceof RemoteConsoleCommandSender)
            return remoteConsoleLocale;

        if(sender instanceof Player)
            return LocaleUtils.toLocale(locales.getLocaleForPlayer((Player) sender));

        return Locale.ENGLISH;
    }
}
