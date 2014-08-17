package com.publicuhc.pluginframework.routing.converter;

import com.publicuhc.pluginframework.routing.converters.CoordinatesValueConverter;
import com.publicuhc.pluginframework.util.Coordinates;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(PowerMockRunner.class)
public class CoordinatesValueConverterTest {

    CoordinatesValueConverter converter;

    @Before
    public void onStartup()
    {
        converter = new CoordinatesValueConverter();
    }

    @Test(expected = ValueConversionException.class)
    public void testTooMany()
    {
        converter.convert("1,20,20,22");
    }

    @Test(expected = ValueConversionException.class)
    public void testTooLittle()
    {
        converter.convert("1");
    }

    @Test(expected = ValueConversionException.class)
    public void testInvalid()
    {
        converter.convert("1,sdjh,22");
    }

    @Test
    public void testTwoCoords()
    {
        Coordinates coordinates = converter.convert("1,10");
        assertThat(coordinates.getX()).isEqualTo(1);
        assertThat(coordinates.getY()).isEqualTo(0);
        assertThat(coordinates.getZ()).isEqualTo(10);

        coordinates = converter.convert("1.987,-290");
        assertThat(coordinates.getX()).isEqualTo(1.987);
        assertThat(coordinates.getY()).isEqualTo(0);
        assertThat(coordinates.getZ()).isEqualTo(-290);

        coordinates = converter.convert("1.987e8,-290");
        assertThat(coordinates.getX()).isEqualTo(198700000);
        assertThat(coordinates.getY()).isEqualTo(0);
        assertThat(coordinates.getZ()).isEqualTo(-290);
    }

    @Test
    public void testThreeCoords()
    {
        Coordinates coordinates = converter.convert("1,27,10");
        assertThat(coordinates.getX()).isEqualTo(1);
        assertThat(coordinates.getY()).isEqualTo(27);
        assertThat(coordinates.getZ()).isEqualTo(10);

        coordinates = converter.convert("1.987,38,-290");
        assertThat(coordinates.getX()).isEqualTo(1.987);
        assertThat(coordinates.getY()).isEqualTo(38);
        assertThat(coordinates.getZ()).isEqualTo(-290);

        coordinates = converter.convert("1.987e8,-299,-290");
        assertThat(coordinates.getX()).isEqualTo(198700000);
        assertThat(coordinates.getY()).isEqualTo(-299);
        assertThat(coordinates.getZ()).isEqualTo(-290);
    }
}
