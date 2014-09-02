package com.publicuhc.pluginframework.locale;

import org.bukkit.command.CommandSender;

import java.util.Locale;

public interface LocaleProvider
{
    public Locale localeForCommandSender(CommandSender sender);
}
