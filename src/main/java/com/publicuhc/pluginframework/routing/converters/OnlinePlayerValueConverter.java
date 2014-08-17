package com.publicuhc.pluginframework.routing.converters;

import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Converts a online player name -> player object
 */
public class OnlinePlayerValueConverter implements ValueConverter<Player> {
    @Override
    public Player convert(String value) {
        Player p = Bukkit.getPlayer(value);
        if(null == p)
            throw new ValueConversionException("Player not found online");
        return p;
    }

    @Override
    public Class<Player> valueType() {
        return Player.class;
    }

    @Override
    public String valuePattern() {
        return null;
    }
}
