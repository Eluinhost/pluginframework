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
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DefaultTranslate implements Translate {

    private final Configurator m_configurator;
    private final TranslateReflection locales;

    @Inject
    protected DefaultTranslate(Configurator configurator, TranslateReflection locales) {
        m_configurator = configurator;
        this.locales = locales;
    }

    @Override
    public String translate(String key, String locale) {
        return translate(key, locale, new HashMap<String, String>());
    }

    @Override
    public String translate(String key, CommandSender sender) {
        return translate(key, getLocaleForSender(sender));
    }

    @Override
    public String translate(String key, String locale, Map<String, String> vars) {
        FileConfiguration configuration = m_configurator.getConfig("translations:" + locale);

        String value = configuration.getString(key);

        if (null == value) {
            value = key;
        }

        for (String s : vars.keySet()) {
            value = value.replaceAll("%" + s + "%", vars.get(s));
        }

        value = ChatColor.translateAlternateColorCodes('&', value);

        return value;
    }

    @Override
    public String translate(String key, CommandSender sender, Map<String, String> vars) {
        return translate(key, getLocaleForSender(sender), vars);
    }

    @Override
    public String translate(String key, String locale, String var, String value) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(var, value);
        return translate(key, locale, map);
    }

    @Override
    public String translate(String key, CommandSender sender, String var, String value) {
        return translate(key, getLocaleForSender(sender), var, value);
    }

    @Override
    public String getLocaleForSender(CommandSender sender) {
        FileConfiguration locales = m_configurator.getConfig("locales");

        String locale = null;

        if (sender instanceof RemoteConsoleCommandSender) {
            locale = locales.getString("remoteConsole");
        } else if (sender instanceof ConsoleCommandSender) {
            locale = locales.getString("console");
        } else if (sender instanceof BlockCommandSender) {
            locale = locales.getString("commandBlock");
        } else if (sender instanceof Player) {
            locale = locales.getString("players." + ((Player) sender).getUniqueId());
        }

        if (null == locale) {
            locale = locales.getString("default");
        }
        return locale;
    }

    @Override
    public void setLocaleForPlayer(Player p, String code) {
        FileConfiguration configuration = m_configurator.getConfig("locales");
        configuration.set("players." + p.getUniqueId().toString(), code);
        m_configurator.saveConfig("locales");
    }
}
