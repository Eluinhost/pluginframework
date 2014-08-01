/*
 * Translate.java
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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public interface Translate {

    /**
     * Translate the string with the key given
     *
     * @param key    the key
     * @param locale the locale to use
     * @return the translated string
     */
    String translate(String key, String locale);

    /**
     * Translate the string with the key given
     *
     * @param key    the key
     * @param sender the sender to get locale for
     * @return the translated string
     */
    String translate(String key, CommandSender sender);

    /**
     * Translate the string and replace the vars with the values given
     *
     * @param key    the key
     * @param locale the locale to use
     * @param vars   the map of keys=>values to replace
     * @return the translated string
     */
    String translate(String key, String locale, Map<String, String> vars);

    /**
     * Translate the string and replace the vars with the values given
     *
     * @param key    the key
     * @param sender the sender to get locale for
     * @param vars   the map of keys=>values to replace
     * @return the translated string
     */
    String translate(String key, CommandSender sender, Map<String, String> vars);

    /**
     * Utility function for translating with 1 var
     *
     * @param key    the key
     * @param var    the var name
     * @param value  the value for the var
     * @param locale the locale to use
     * @return the translated string
     */
    String translate(String key, String locale, String var, String value);

    /**
     * Utility function for translating with 1 var
     *
     * @param key    the key
     * @param var    the var name
     * @param value  the value for the var
     * @param sender the sender to get locale for
     * @return the translated string
     */
    String translate(String key, CommandSender sender, String var, String value);

    /**
     * Get the locale for the sender or the default if not found
     *
     * @param sender the sender
     * @return the locale name
     */
    String getLocaleForSender(CommandSender sender);

    /**
     * Set the locale for the given player
     *
     * @param p    the player
     * @param code the locale code
     */
    void setLocaleForPlayer(Player p, String code);
}
