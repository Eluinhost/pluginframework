/*
 * BukkitLocaleProvider.java
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

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.publicuhc.pluginframework.configuration.Configurator;
import org.apache.commons.lang.LocaleUtils;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Locale;

public class BukkitLocaleProvider implements LocaleProvider
{
    private final LocaleFetcher fetcher;

    private Locale commandBlockLocale;
    private Locale remoteConsoleLocale;
    private Locale consoleLocale;

    @Inject
    public BukkitLocaleProvider(LocaleFetcher fetcher, @Named("defaultLocale") Locale defaultLocale)
    {
        this.fetcher = fetcher;
        commandBlockLocale = defaultLocale;
        remoteConsoleLocale = defaultLocale;
        consoleLocale = defaultLocale;
    }

    @Inject(optional = true)
    protected void setConfigurator(Configurator configurator)
    {
        Optional<FileConfiguration> configurationOptional = configurator.getConfig("locales");

        if(configurationOptional.isPresent()) {
            FileConfiguration config = configurationOptional.get();
            commandBlockLocale = LocaleUtils.toLocale(config.getString("commandBlock", "en_US"));
            remoteConsoleLocale = LocaleUtils.toLocale(config.getString("remoteConsole", "en_US"));
            consoleLocale = LocaleUtils.toLocale(config.getString("console", "en_US"));
        }
    }

    @Override
    public Locale localeForCommandSender(CommandSender sender)
    {
        if(sender instanceof BlockCommandSender)
            return commandBlockLocale;

        if(sender instanceof ConsoleCommandSender)
            return consoleLocale;

        if(sender instanceof RemoteConsoleCommandSender)
            return remoteConsoleLocale;

        if(sender instanceof Player)
            return LocaleUtils.toLocale(fetcher.getLocaleForPlayer((Player) sender));

        return Locale.ENGLISH;
    }
}
