/*
 * ExactRouteMatchPredicate.java
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

public class ExactRouteMatchPredicate implements Predicate<CommandRoute>
{
    private final String route;

    /**
     * Filters based on if the routes startswith matches the given route exactly case insensitive
     * @param route the route to check against
     */
    public ExactRouteMatchPredicate(List<String> route)
    {
        this.route = Joiner.on(" ").join(route);
    }

    @Override
    public boolean apply(@Nullable CommandRoute commandRoute)
    {
        return commandRoute != null && Joiner.on(" ").join(commandRoute.getStartsWith()).equalsIgnoreCase(route);
    }
}
