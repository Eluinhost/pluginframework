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
import org.apache.commons.lang.LocaleUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginLogger;

import java.util.*;

public class DefaultTranslate implements Translate {

    private final TranslateReflection locales;
    private final YamlControl controller;

    @Inject
    protected DefaultTranslate(TranslateReflection locales, YamlControl controller, PluginLogger logger) {
        this.locales = locales;
        this.controller = controller;
    }

    protected ResourceBundle getConfigForLocale(Locale locale)
    {
        return YamlResourceBundle.getBundle("lang", locale, controller);
    }

    @Override
    public String translate(String key, Locale locale) {
        return translate(key, locale, new HashMap<String, String>());
    }

    @Override
    public String translate(String key, CommandSender sender) {
        return translate(key, getLocaleForSender(sender));
    }

    @Override
    public String translate(String key, Locale locale, Map<String, String> vars) {
        ResourceBundle configuration = getConfigForLocale(locale);

        String value = null;
        try {
            value = configuration.getString(key);
        }
        catch (MissingResourceException ignored){}

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
    public String translate(String key, Locale locale, String var, String value) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(var, value);
        return translate(key, locale, map);
    }

    @Override
    public String translate(String key, CommandSender sender, String var, String value) {
        return translate(key, getLocaleForSender(sender), var, value);
    }

    @Override
    public Locale getLocaleForSender(CommandSender sender) {
        //TODO separate settings for remoteconsole/console/commandblock/broadcast
        if(sender instanceof Player)
            return LocaleUtils.toLocale(locales.getLocaleForPlayer((Player) sender));
        else
            return Locale.ENGLISH;
    }
}
