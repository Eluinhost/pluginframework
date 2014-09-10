/*
 * OnlinePlayerValueConverter.java
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
            throw new ValueConversionException("Player " + value + " is not online");
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
        return allowStar ? "playerName/*" : "playerName" ;
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
