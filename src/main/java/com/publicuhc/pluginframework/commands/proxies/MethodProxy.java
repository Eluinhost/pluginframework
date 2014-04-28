/*
 * MethodProxy.java
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

package com.publicuhc.pluginframework.commands.proxies;

import com.publicuhc.pluginframework.commands.routes.Route;

import java.lang.reflect.Method;

public interface MethodProxy {

    /**
     * @param route the route to be matched to run
     */
    void setRoute(Route route);

    /**
     * @return the route to be matched on
     */
    Route getRoute();

    /**
     * @param object the instance to run against
     */
    void setInstance(Object object);

    /**
     * @return the instance to run with
     */
    Object getInstance();

    /**
     * @param method the method to run on command run
     */
    void setCommandMethod(Method method);

    /**
     * @return the method to run the command on
     */
    Method getCommandMethod();

}
