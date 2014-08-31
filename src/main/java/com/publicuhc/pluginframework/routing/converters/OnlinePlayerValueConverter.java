package com.publicuhc.pluginframework.routing.converters;

import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        return "PlayerName";
    }

    /**
     * Useful for when using player converter in nonOptions(), recombines list of arrays into a single set removing duplicates
     *
     * @param playerLists the list of player arrays
     * @return combined set
     */
    public static Set<Player> recombinePlayerLists(List<Player[]> playerLists)
    {
        //make a new hashset to put players in
        Set<Player> players = new HashSet<Player>();
        for(Player[] comboPlayers : playerLists) {
            Collections.addAll(players, comboPlayers);
        }
        return players;
    }
}
