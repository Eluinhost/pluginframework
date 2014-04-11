/*
 * CommandProxy.java
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

package com.publicuhc.commands.proxies;

import com.publicuhc.commands.requests.CommandRequest;

import java.lang.reflect.Method;

public class CommandProxy extends DefaultMethodProxy {

    /**
     * Trigger this command
     *
     * @param request the request params
     */
    public void trigger(CommandRequest request) throws ProxyTriggerException {
        Object instance = getInstance();
        Method method = getCommandMethod();
        try {
            method.invoke(instance, request);
        } catch (Exception e) {
            throw new ProxyTriggerException(e);
        }
    }
}
