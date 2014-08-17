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

    OnlinePlayerValueConverter converter;
    Player player;

    @Before
    public void onStartup()
    {
        converter = new OnlinePlayerValueConverter();
        mockStatic(Bukkit.class);

        //make ghowden not online, make eluinhost online
        when(Bukkit.getPlayer("ghowden")).thenReturn(null);
        player = mock(Player.class);
        when(Bukkit.getPlayer("eluinhost")).thenReturn(player);
    }

    @Test
    public void testValidPlayer()
    {
        Player player = converter.convert("eluinhost");
        assertThat(player).isSameAs(this.player);
    }

    @Test(expected = ValueConversionException.class)
    public void testNotFound()
    {
        converter.convert("ghowden");
    }
}
