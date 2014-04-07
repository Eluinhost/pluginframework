package com.publicuhc.translate;

import java.util.Map;

public interface Translate {

    /**
     * Translate the string with the key given
     * @param key the key
     * @return the translated string
     */
    String translate(String key);

    /**
     * Translate the string and replace the vars with the values given
     * @param key the key
     * @param vars the map of keys=>values to replace
     * @return the translated string
     */
    String translate(String key, Map<String, String> vars);

    /**
     * Utility function for translating with 1 var
     * @param key the key
     * @param var the var name
     * @param value the value for the var
     * @return the translated string
     */
    String translate(String key, String var, String value);
}
