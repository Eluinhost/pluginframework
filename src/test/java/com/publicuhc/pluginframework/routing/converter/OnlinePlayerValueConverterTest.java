/*
 * OnlinePlayerValueConverterTest.java
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

package com.publicuhc.pluginframework.routing.converter;

import com.publicuhc.pluginframework.routing.converters.OnlinePlayerValueConverter;
import joptsimple.ValueConversionException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class OnlinePlayerValueConverterTest {

    Player player;

    @Before
    public void onStartup()
    {
        mockStatic(Bukkit.class);

        //make ghowden not online, make eluinhost online
        when(Bukkit.getPlayer("ghowden")).thenReturn(null);
        player = mock(Player.class);
        when(Bukkit.getPlayer("eluinhost")).thenReturn(player);
        when(Bukkit.getOnlinePlayers()).thenReturn(new Player[]{player});
    }

    @Test
    public void testValidPlayer()
    {
        OnlinePlayerValueConverter converter = new OnlinePlayerValueConverter(false);
        Player[] players = converter.convert("eluinhost");
        assertThat(players).containsExactly(player);
    }

    @Test(expected = ValueConversionException.class)
    public void testNotFound()
    {
        OnlinePlayerValueConverter converter = new OnlinePlayerValueConverter(false);
        converter.convert("ghowden");
    }

    @Test
    public void testAllOnline()
    {
        OnlinePlayerValueConverter converter = new OnlinePlayerValueConverter(true);
        Player[] players = converter.convert("*");
        assertThat(players).containsExactly(player);
    }

    @Test(expected = ValueConversionException.class)
    public void testStar()
    {
        OnlinePlayerValueConverter converter = new OnlinePlayerValueConverter(false);
        converter.convert("*");
    }

    @Test
    public void test_recombine_player_lists()
    {
        List<Player[]> combined = new ArrayList<Player[]>();

        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        Player player3 = mock(Player.class);

        //emulate a * arg
        combined.add(new Player[]{player1, player2, player3});

        //emulate single players
        combined.add(new Player[]{player1});
        combined.add(new Player[]{player3});

        assertThat(OnlinePlayerValueConverter.recombinePlayerLists(combined)).contains(player1, player2, player3);
    }
}
