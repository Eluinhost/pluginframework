/*
 * SubroutePredicate.java
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

package com.publicuhc.pluginframework.routing.functions;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.publicuhc.pluginframework.routing.CommandRoute;

import javax.annotation.Nullable;
import java.util.List;

public class SubroutePredicate implements Predicate<CommandRoute>
{
    private final String selectedRoute;
    private final int expectedLength;

    /**
     * Allows any route with startsWith().length = selectedRoute.size()  + 1
     * Also checks case insensitive the route startswith starts with the selected route.
     *
     * Ex.
     * selectedRoute = 'a', 'b'
     *
     * Would match: ['a', 'b', 'c'], ['a', 'b', 'd']
     * Wouldn't match: ['a', 'b'], ['a', 'b', 'c', 'd'], ['b', 'a']
     *
     * @param selectedRoute the current route to match
     */
    public SubroutePredicate(List<String> selectedRoute)
    {
        this.selectedRoute = Joiner.on(" ").join(selectedRoute).toLowerCase();
        expectedLength = selectedRoute.size() + 1;
    }

    @Override
    public boolean apply(@Nullable CommandRoute commandRoute)
    {
        if(commandRoute == null) {
            return false;
        }

        String[] startsWith = commandRoute.getStartsWith();

        if(startsWith.length == expectedLength) {
            String loweredStartsWith = Joiner.on(" ").join(startsWith).toLowerCase();
            if(loweredStartsWith.startsWith(selectedRoute)) {
                return true;
            }
        }
        return false;
    }
}
