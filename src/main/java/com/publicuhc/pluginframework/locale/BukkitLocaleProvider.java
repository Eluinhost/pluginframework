package com.publicuhc.pluginframework.locale;

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
    private Locale remoteConsoleLocale = Locale.ENGLISH;
    private Locale consoleLocale = Locale.ENGLISH;

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
        FileConfiguration config = configurator.getConfig("locales");

        commandBlockLocale = LocaleUtils.toLocale(config.getString("commandBlock", "en_US"));
        remoteConsoleLocale = LocaleUtils.toLocale(config.getString("remoteConsole", "en_US"));
        consoleLocale = LocaleUtils.toLocale(config.getString("console", "en_US"));
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
