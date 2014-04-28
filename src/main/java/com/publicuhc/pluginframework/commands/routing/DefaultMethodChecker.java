/*
 * DefaultMethodChecker.java
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

package com.publicuhc.pluginframework.commands.routing;

import com.google.inject.Inject;
import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
import com.publicuhc.pluginframework.commands.annotation.RouteInfo;
import com.publicuhc.pluginframework.commands.annotation.TabCompletion;
import com.publicuhc.pluginframework.commands.exceptions.AnnotationMissingException;
import com.publicuhc.pluginframework.commands.exceptions.InvalidMethodParametersException;
import com.publicuhc.pluginframework.commands.exceptions.InvalidReturnTypeException;
import com.publicuhc.pluginframework.commands.requests.CommandRequest;
import org.bukkit.plugin.PluginLogger;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultMethodChecker implements MethodChecker {

    private final Logger m_logger;

    @Inject
    protected DefaultMethodChecker(PluginLogger logger) {
        m_logger = logger;
    }

    @Override
    public void checkTabComplete(Method method) throws InvalidMethodParametersException, AnnotationMissingException, InvalidReturnTypeException {
        checkForCommandRequestParameter(method);
        checkForAnnotationPresent(TabCompletion.class, method);

        //only allow list returns
        if (!List.class.isAssignableFrom(method.getReturnType())) {
            throw new InvalidReturnTypeException();
        }

        Type type = method.getGenericReturnType();

        //make sure its a string parameter
        ParameterizedType ptype = (ParameterizedType) type;
        Type[] types = ptype.getActualTypeArguments();
        if (types.length != 1 || !String.class.isAssignableFrom((Class) types[0])) {
            throw new InvalidReturnTypeException();
        }
    }

    @Override
    public void checkCommandMethod(Method method) throws InvalidMethodParametersException, AnnotationMissingException {
        checkForCommandRequestParameter(method);
        checkForAnnotationPresent(CommandMethod.class, method);
    }

    @Override
    public void checkRouteInfo(Method method) throws InvalidMethodParametersException, AnnotationMissingException {
        checkForBuilderParameter(method);
        checkForAnnotationPresent(RouteInfo.class, method);
    }

    protected void checkForCommandRequestParameter(Method method) throws InvalidMethodParametersException {
        if (method.getParameterTypes().length != 1 || !CommandRequest.class.isAssignableFrom(method.getParameterTypes()[0])) {
            m_logger.log(Level.SEVERE, "Method " + method.getName() + " has incorrect parameters");
            throw new InvalidMethodParametersException();
        }
    }

    protected void checkForBuilderParameter(Method method) throws InvalidMethodParametersException {
        if (method.getParameterTypes().length != 1 || !RouteBuilder.class.isAssignableFrom(method.getParameterTypes()[0])) {
            m_logger.log(Level.SEVERE, "Method " + method.getName() + " has incorrect parameters");
            throw new InvalidMethodParametersException();
        }
    }

    protected void checkForAnnotationPresent(Class annotation, Method method) throws AnnotationMissingException {
        if(null == method.getAnnotation(annotation)) {
            m_logger.log(Level.SEVERE, "Method " + method.getName() + " does not have the @"+annotation.getName()+" annotation");
            throw new AnnotationMissingException();
        }
    }
}
