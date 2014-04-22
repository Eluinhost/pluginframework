/*
 * SimplePair.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
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

public class SimplePair<K, V> {

    private final K m_key;

    /**
     * @return the key for the pair
     */
    public K getKey() {
        return m_key;
    }

    private final V m_value;

    /**
     * @return the value for the pair
     */
    public V getValue() {
        return m_value;
    }

    /**
     * A Pair of values
     * @param key the key
     * @param value the value
     */
    public SimplePair(K key, V value) {
        m_key = key;
        m_value = value;
    }
}