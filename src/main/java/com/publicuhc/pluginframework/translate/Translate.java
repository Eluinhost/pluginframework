/*
 * Translate.java
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

import org.bukkit.command.CommandSender;

import java.util.Locale;

public interface Translate {

    /**
     * Translate the string with the key given
     *
     * @param key    the key
     * @param locale the locale to use
     * @param params the values to use in String.format
     * @return the translated string
     */
    String translate(String key, Locale locale, Object... params);

    /**
     * Translate the string with the key given
     *
     * @param key    the key
     * @param sender the sender whose locale will be used
     * @param params the values to use in String.format
     * @return the translated string
     */
    String translate(String key, CommandSender sender, Object... params);

    /**
     * Send a translated key to the sender
     *
     * @param key    the key to lookup
     * @param sender the sender whose locale will be used
     * @param params the values to use in String.format
     */
    void sendMessage(String key, CommandSender sender, Object... params);

    /**
     * Send a message to all players using their own locale
     *
     * @param key    the key to lookup
     * @param params the values to use in String.format
     */
    void broadcastMessage(String key, Object... params);

    /**
     * Send a message to all players with the permission using their own locale
     *
     * @param key    the key to lookup
     * @param params the values to use in String.format
     */
    void broadcastMessageForPermission(String permission, String key, Object... params);
}
