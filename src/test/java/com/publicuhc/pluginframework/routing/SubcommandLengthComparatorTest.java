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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class SubcommandLengthComparatorTest
{
    @Test
    public void test_comparator()
    {
        CommandRoute routeLength0 = mock(CommandRoute.class);
        when(routeLength0.getStartsWith()).thenReturn(new String[]{});

        CommandRoute routeLength1 = mock(CommandRoute.class);
        when(routeLength1.getStartsWith()).thenReturn(new String[]{""});

        CommandRoute routeLength2 = mock(CommandRoute.class);
        when(routeLength2.getStartsWith()).thenReturn(new String[]{"", ""});

        SubcommandLengthComparator comparator = new SubcommandLengthComparator(true);

        //test
        assertThat(comparator.compare(routeLength0, routeLength1)).isEqualTo(1);
        assertThat(comparator.compare(routeLength1, routeLength0)).isEqualTo(-1);
        assertThat(comparator.compare(routeLength1, routeLength2)).isEqualTo(1);
        assertThat(comparator.compare(routeLength2, routeLength1)).isEqualTo(-1);
        assertThat(comparator.compare(routeLength2, routeLength0)).isEqualTo(-1);
        assertThat(comparator.compare(routeLength0, routeLength2)).isEqualTo(1);
        assertThat(comparator.compare(routeLength1, routeLength1)).isEqualTo(0);
        assertThat(comparator.compare(routeLength2, routeLength2)).isEqualTo(0);
        assertThat(comparator.compare(routeLength0, routeLength0)).isEqualTo(0);

        List<CommandRoute> routeList = new ArrayList<CommandRoute>();
        routeList.add(routeLength1);
        routeList.add(routeLength2);
        routeList.add(routeLength0);
        routeList.add(routeLength1);

        Collections.sort(routeList, comparator);
        assertThat(routeList).containsExactly(routeLength2, routeLength1, routeLength1, routeLength0);

        comparator = new SubcommandLengthComparator(false);

        //test
        assertThat(comparator.compare(routeLength0, routeLength1)).isEqualTo(-1);
        assertThat(comparator.compare(routeLength1, routeLength0)).isEqualTo(1);
        assertThat(comparator.compare(routeLength1, routeLength2)).isEqualTo(-1);
        assertThat(comparator.compare(routeLength2, routeLength1)).isEqualTo(1);
        assertThat(comparator.compare(routeLength2, routeLength0)).isEqualTo(1);
        assertThat(comparator.compare(routeLength0, routeLength2)).isEqualTo(-1);
        assertThat(comparator.compare(routeLength1, routeLength1)).isEqualTo(0);
        assertThat(comparator.compare(routeLength2, routeLength2)).isEqualTo(0);
        assertThat(comparator.compare(routeLength0, routeLength0)).isEqualTo(0);

        Collections.sort(routeList, comparator);
        assertThat(routeList).containsExactly(routeLength0, routeLength1, routeLength1, routeLength2);
    }
}
