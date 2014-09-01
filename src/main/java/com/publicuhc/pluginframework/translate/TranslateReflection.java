package com.publicuhc.pluginframework.translate;

import org.bukkit.entity.Player;

public interface TranslateReflection
{
    /**
     * Fetch the locale from the player object
     *
     * @param p the player to check
     * @return the player's locale
     */
    public String getLocaleForPlayer(Player p);
}
