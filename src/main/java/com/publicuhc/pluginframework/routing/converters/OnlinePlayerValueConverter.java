package com.publicuhc.pluginframework.routing.converters;

import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Converts a online player name -> player object
 */
public class OnlinePlayerValueConverter implements ValueConverter<Player[]> {

    private final boolean allowStar;

    /**
     * A converter that converts player names to player objects
     * @param allowStar if true, converts * to all online players
     */
    public OnlinePlayerValueConverter(boolean allowStar)
    {
        this.allowStar = allowStar;
    }

    @Override
    public Player[] convert(String value)
    {
        if(allowStar && value.equals("*"))
            return Bukkit.getOnlinePlayers();
        @SuppressWarnings("deprecation")
        Player p = Bukkit.getPlayer(value);
        if(null == p)
            throw new ValueConversionException("Player not found online");
        return new Player[]{p};
    }

    @Override
    public Class<Player[]> valueType()
    {
        return Player[].class;
    }

    @Override
    public String valuePattern()
    {
        return null;
    }
}
