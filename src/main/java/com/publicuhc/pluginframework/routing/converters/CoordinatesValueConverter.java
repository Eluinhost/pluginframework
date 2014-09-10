/*
 * CoordinatesValueConverter.java
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
            throw new ValueConversionException("Invalid coordinates format: " + value);

        Double[] parsed = new Double[parts.length];
        try {
            for (int i = 0; i < parts.length; i++) {
                parsed[i] = Double.parseDouble(parts[i]);
            }
        } catch (NumberFormatException ex) {
            throw new ValueConversionException("Invalid coordinate value: " + value);
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
