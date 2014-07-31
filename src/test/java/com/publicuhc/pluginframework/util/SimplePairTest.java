/*
 * SimplePairTest.java
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

package com.publicuhc.pluginframework.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
public class SimplePairTest {

    @Test
    public void testSimplePair() {
        String testString = "testString";
        Integer testInt = 20;
        SimplePair<String, Integer> pair = new SimplePair<String, Integer>(testString, testInt);

        assertThat(pair.getKey(), is(instanceOf(String.class)));
        assertThat(pair.getValue(), is(instanceOf(Integer.class)));

        assertThat(pair.getKey(), is(sameInstance(testString)));
        assertThat(pair.getValue(), is(sameInstance(testInt)));
    }
}
