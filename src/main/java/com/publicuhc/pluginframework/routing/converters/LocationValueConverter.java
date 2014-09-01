package com.publicuhc.pluginframework.routing.converters;

import com.publicuhc.pluginframework.util.Coordinates;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Converts locations in the formats:
 *   world,x,y,z
 *   world,x,z
 */
public class LocationValueConverter implements ValueConverter<Location> {
    @Override
    public Location convert(String value) {
        int firstComma = value.indexOf(",");
        if(-1 == firstComma)
            throw new ValueConversionException("Invalid Location format: " + value);

        String worldname = value.substring(0, firstComma);
        World world = Bukkit.getWorld(worldname);
        if(null == world)
            throw new ValueConversionException("Invalid world name for: " + value);

        String coordinateString = value.substring(firstComma + 1);
        CoordinatesValueConverter converter = new CoordinatesValueConverter();
        Coordinates coords = converter.convert(coordinateString);

        return coords.asLocationForWorld(world);
    }

    @Override
    public Class<Location> valueType() {
        return Location.class;
    }

    @Override
    public String valuePattern() {
        return "Location: world,x,y,z OR world,x,z";
    }
}
