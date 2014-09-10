/*
 * SubcommandLengthComparatorTest.java
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
