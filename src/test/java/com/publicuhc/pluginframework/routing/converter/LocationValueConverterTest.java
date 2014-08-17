package com.publicuhc.pluginframework.routing.converter;

import com.publicuhc.pluginframework.routing.converters.LocationValueConverter;
import joptsimple.ValueConversionException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
public class LocationValueConverterTest {

    LocationValueConverter converter;
    World world;

    @Before
    public void onStartup()
    {
        converter = new LocationValueConverter();
        mockStatic(Bukkit.class);
        world = mock(World.class);
        when(Bukkit.getWorld("valid")).thenReturn(world);
        when(Bukkit.getWorld("invalid")).thenReturn(null);
    }

    @Test
    public void testValidTwo()
    {
        Location loc = converter.convert("valid,-1,20");

        assertThat(loc.getWorld()).isSameAs(world);
        assertThat(loc.getX()).isEqualTo(-1);
        assertThat(loc.getY()).isEqualTo(0);
        assertThat(loc.getZ()).isEqualTo(20);
    }

    @Test
    public void testValidThree()
    {
        Location loc = converter.convert("valid,-1,92,20");

        assertThat(loc.getWorld()).isSameAs(world);
        assertThat(loc.getX()).isEqualTo(-1);
        assertThat(loc.getY()).isEqualTo(92);
        assertThat(loc.getZ()).isEqualTo(20);
    }

    @Test(expected = ValueConversionException.class)
    public void testInvalidWorld()
    {
        converter.convert("invalid,-1,20");
    }

    @Test(expected = ValueConversionException.class)
    public void testInvalid()
    {
        converter.convert("jskd");
    }

    @Test(expected = ValueConversionException.class)
    public void testInvalidCoord()
    {
        converter.convert("valid,-1,20,298,2098");
    }
}
