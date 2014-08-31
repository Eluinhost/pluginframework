/*
 * SubcommandLengthComparator.java
 *
 * Copyright (c) 2014. Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of PluginFramework.
 *
 * PluginFramework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PluginFramework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PluginFramework.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.pluginframework.routing;

import java.util.Comparator;

class SubcommandLengthComparator implements Comparator<CommandRoute>
{

    private boolean mostApplicable;

    public SubcommandLengthComparator(boolean mostApplicable)
    {
        this.mostApplicable = mostApplicable;
    }

    @Override
    public int compare(CommandRoute route1, CommandRoute route2)
    {
        int r1 = route1.getStartsWith().length;
        int r2 = route2.getStartsWith().length;

        return mostApplicable ? Integer.compare(r1, r2) : Integer.compare(r2, r1);
    }
}
