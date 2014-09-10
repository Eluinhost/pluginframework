/*
 * ApplicableRoutePredicate.java
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

public class ApplicableRoutePredicate implements Predicate<CommandRoute>
{
    private final String actual;
    private final boolean subcommands;

    /**
     * Fetches all routes whose startsWith match the given args
     * @param args the arguments to match against
     * @param subcommands whether to show the route + subcommands (true) or route + parents (false)
     */
    public ApplicableRoutePredicate(String[] args, boolean subcommands)
    {
        actual = Joiner.on(" ").join(args).toLowerCase();
        this.subcommands = subcommands;
    }

    @Override
    public boolean apply(@Nullable CommandRoute commandRoute)
    {
        if(null == commandRoute) {
            return false;
        }

        String startsWith = Joiner.on(" ").join(commandRoute.getStartsWith());

        return subcommands ? startsWith.startsWith(actual) : actual.startsWith(startsWith);
    }
}
