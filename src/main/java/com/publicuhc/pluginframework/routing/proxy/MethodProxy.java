/*
 * MethodProxy.java
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

package com.publicuhc.pluginframework.routing.proxy;

public interface MethodProxy
{

    /**
     * @param instance the instance to run against
     */
    public void setInstance(Object instance);

    /**
     * @return the instance to run with
     */
    public Object getInstance();

    /**
     * Invoke the method with the given arguments
     *
     * @param args the args to call the method with
     * @return the object returned by the method
     * @throws java.lang.Throwable any error caused during invocation
     */
    public Object invoke(Object... args) throws Throwable;
}
