/*
 * DefaultMethodProxy.java
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

public class DefaultMethodProxy implements MethodProxy {

    private Route m_route;
    private Method m_method;
    private Object m_instance;

    @Override
    public void setRoute(Route route) {
        m_route = route;
    }

    @Override
    public void setInstance(Object object) {
        m_instance = object;
    }

    @Override
    public void setCommandMethod(Method method) {
        m_method = method;
    }

    @Override
    public Route getRoute() {
        return m_route;
    }

    @Override
    public Object getInstance() {
        return m_instance;
    }

    @Override
    public Method getCommandMethod() {
        return m_method;
    }
}
