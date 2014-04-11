package com.publicuhc.translate;

import org.bukkit.command.CommandSender;

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
     * Translate the string and replace the vars with the values given
     *
     * @param key    the key
     * @param locale the locale to use
     * @param vars   the map of keys=>values to replace
     * @return the translated string
     * @throws com.publicuhc.translate.exceptions.LocaleNotFoundError if the locale supplied is invalid
     */
    String translate(String key, String locale, Map<String, String> vars);

    /**
     * Utility function for translating with 1 var
     *
     * @param key    the key
     * @param var    the var name
     * @param value  the value for the var
     * @param locale the locale to use
     * @return the translated string
     * @throws com.publicuhc.translate.exceptions.LocaleNotFoundError if the locale supplied is invalid
     */
    String translate(String key, String locale, String var, String value);

    /**
     * Get the locale for the sender or the default if not found
     *
     * @param sender the sender
     * @return the locale name
     * @throws com.publicuhc.translate.exceptions.LocaleNotFoundError if the locale supplied is invalid
     */
    String getLocaleForSender(CommandSender sender);
}
