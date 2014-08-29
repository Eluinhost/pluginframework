package com.publicuhc.pluginframework.routing.converters;

import com.publicuhc.pluginframework.util.Coordinates;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

/**
 * Converts arguments in the format x,y,z to a coordinate.
 * If supplied in the format x,z then y will be set to 0
 */
public class CoordinatesValueConverter implements ValueConverter<Coordinates> {
    @Override
    public Coordinates convert(String value) {
        String[] parts = value.split(",");
        if(parts.length < 2 || parts.length > 3)
            throw new ValueConversionException("Invalid coordinates format: " + value + ". Must use the format x,y,z or x,z");

        Double[] parsed = new Double[parts.length];
        try {
            for (int i = 0; i < parts.length; i++) {
                parsed[i] = Double.parseDouble(parts[i]);
            }
        } catch (NumberFormatException ex) {
            throw new ValueConversionException("Invalid coordinates: " + value);
        }

        double x = parsed[0];
        double y = parsed.length == 3 ? parsed[1] : 0;
        double z = parsed[parsed.length - 1];

        return new Coordinates(x, y, z);
    }

    @Override
    public Class<Coordinates> valueType() {
        return Coordinates.class;
    }

    @Override
    public String valuePattern() {
        return null;
    }
}
