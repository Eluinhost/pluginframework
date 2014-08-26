/*
 * DefaultRoutingMethodParser.java
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

package com.publicuhc.pluginframework.routing.parser;

import com.publicuhc.pluginframework.routing.CommandMethod;
import com.publicuhc.pluginframework.routing.CommandRequest;
import com.publicuhc.pluginframework.routing.CommandRoute;
import com.publicuhc.pluginframework.routing.DefaultCommandRoute;
import com.publicuhc.pluginframework.routing.exception.AnnotationMissingException;
import com.publicuhc.pluginframework.routing.exception.CommandParseException;
import com.publicuhc.pluginframework.routing.help.BukkitHelpFormatter;
import com.publicuhc.pluginframework.routing.proxy.MethodProxy;
import com.publicuhc.pluginframework.routing.proxy.ReflectionMethodProxy;
import joptsimple.OptionParser;
import joptsimple.OptionSpec;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DefaultRoutingMethodParser extends RoutingMethodParser
{

    /**
     * Returns an OptionParser after it has been pased through a method
     * of the same name but only one argument of type OptionParser
     *
     * @param method   the original method
     * @param instance the instance to run on
     * @return the optionparser after being run through the method
     * @throws NoSuchMethodException if the method cannot be found
     */
    protected OptionParser getOptionsForMethod(Method method, Object instance) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method optionsMethod = instance.getClass().getMethod(method.getName(), OptionParser.class);

        if(!(optionsMethod.getReturnType().equals(Void.TYPE)))
            throw new NoSuchMethodException("Options method does not return a String[]");

        //make a new parser and invoke the method with it
        OptionParser parser = new OptionParser();
        optionsMethod.invoke(instance, parser);

        return parser;
    }

    protected boolean areCommandMethodParametersCorrect(Method method)
    {
        Class[] types = method.getParameterTypes();

        return types.length == 1 && types[0].equals(CommandRequest.class);
    }

    @Override
    public CommandRoute parseCommandMethodAnnotation(Method method, Object instance) throws CommandParseException
    {
        CommandMethod annotation = getAnnotation(method, CommandMethod.class);

        if(null == annotation)
            throw new AnnotationMissingException(method, CommandMethod.class);

        if(!areCommandMethodParametersCorrect(method))
            throw new CommandParseException("Invalid command method parameters at " + method.getName());

        OptionParser optionParser;

        //get our option parser for this command
        if(annotation.options()) {
            try {
                optionParser = getOptionsForMethod(method, instance);
            } catch(Exception e) {
                throw new CommandParseException("Exception occured when trying to run the options method for " + method.getName(), e);
            }
        } else {
            optionParser = new OptionParser();
        }

        optionParser.formatHelpWith(new BukkitHelpFormatter());
        OptionSpec helpSpec = optionParser.accepts(annotation.helpOption(), "Shows help").forHelp();

        MethodProxy proxy = new ReflectionMethodProxy(instance, method);

        return new DefaultCommandRoute(annotation.command(), annotation.permission(), annotation.allowedSenders(), proxy, optionParser, helpSpec);
    }

    @Override
    public boolean hasCommandMethodAnnotation(Method method)
    {
        return getAnnotation(method, CommandMethod.class) != null;
    }
}
