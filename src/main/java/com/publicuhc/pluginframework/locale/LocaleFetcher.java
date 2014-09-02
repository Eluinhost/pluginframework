package com.publicuhc.pluginframework.locale;

import org.bukkit.entity.Player;

public interface LocaleFetcher
{
    /**
     * Fetch the locale from the player object
     *
     * @param p the player to check
     * @return the player's locale
     */
    public String getLocaleForPlayer(Player p);
}
