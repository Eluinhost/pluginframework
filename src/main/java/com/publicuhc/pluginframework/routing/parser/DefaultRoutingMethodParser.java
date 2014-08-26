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
import joptsimple.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class DefaultRoutingMethodParser extends RoutingMethodParser
{

    private final Field converterFieldArguments;
    private final Field converterFieldNonOptions;

    /**
     * Used to create CommandRoute objects from a method with an @CommandMethod annotation
     */
    public DefaultRoutingMethodParser()
    {
        //setup the reflection to get the ValueConverter from the option specs
        Field argConverterField = null;
        Field optionsConverterField = null;
        try {
            //get the converter field and allow it to be accessed via reflection
            argConverterField = ArgumentAcceptingOptionSpec.class.getDeclaredField("converter");
            argConverterField.setAccessible(true);

            optionsConverterField = NonOptionArgumentSpec.class.getDeclaredField("converter");
            optionsConverterField.setAccessible(true);
        } catch(NoSuchFieldException e) {
            e.printStackTrace();
        }

        this.converterFieldArguments = argConverterField;
        this.converterFieldNonOptions = optionsConverterField;
    }

    /**
     * Gets a map of option name->converted class, adds 'arguments' for nonOptions. Ignores options without arguments
     *
     * @param parser the parser to use to generate the map
     * @return map of options to their class
     */
    protected Map<String, Class> getParameters(OptionParser parser)
    {
        Map<String, Class> parameterTypes = new HashMap<String, Class>();

        for(Map.Entry<String, OptionSpec<?>> option : parser.recognizedOptions().entrySet()) {

            //get the name of the option
            OptionSpec optionSpec = option.getValue();

            //if its the non options we add it to 'arguments'
            if(optionSpec instanceof NonOptionArgumentSpec) {
                NonOptionArgumentSpec nonOptionsSpec = (NonOptionArgumentSpec) optionSpec;
                try {
                    ValueConverter converter = (ValueConverter) converterFieldNonOptions.get(nonOptionsSpec);
                    Class convertClass = converter == null ? String.class : converter.valueType();
                    parameterTypes.put("arguments", convertClass);
                } catch(IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            //if its an an option with an argument we add it as it's option name
            if(optionSpec instanceof ArgumentAcceptingOptionSpec) {
                ArgumentAcceptingOptionSpec argspec = (ArgumentAcceptingOptionSpec) optionSpec;

                //only do it for options that accept arguments
                if(argspec.acceptsArguments()) {
                    try {
                        ValueConverter converter = (ValueConverter) converterFieldArguments.get(argspec);
                        Class convertClass = converter == null ? String.class : converter.valueType();
                        parameterTypes.put(option.getKey(), convertClass);
                    } catch(IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return parameterTypes;
    }

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
        Method optionsMethod = instance.getClass().getMethod(method.getName(), OptionDeclarer.class);

        if(!(optionsMethod.getReturnType().equals(Void.TYPE)))
            throw new NoSuchMethodException("Options method shouldn't return anything");

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

        //check the @commandmethod parameters are all present and correct
        if(!areCommandMethodParametersCorrect(method))
            throw new CommandParseException("Invalid command method parameters at " + method.getName());

        //add the help formatter and add the default help option
        optionParser.formatHelpWith(new BukkitHelpFormatter());
        OptionSpec helpSpec = optionParser.accepts(annotation.helpOption(), "Shows help").forHelp();

        //setup the proxy and create the route
        MethodProxy proxy = new ReflectionMethodProxy(instance, method);
        return new DefaultCommandRoute(annotation.command(), annotation.permission(), annotation.allowedSenders(), proxy, optionParser, helpSpec);
    }

    @Override
    public boolean hasCommandMethodAnnotation(Method method)
    {
        return getAnnotation(method, CommandMethod.class) != null;
    }
}
