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

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

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
}
