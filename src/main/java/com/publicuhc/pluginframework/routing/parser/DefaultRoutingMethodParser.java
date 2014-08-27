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

import com.google.common.collect.HashMultiset;
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
import org.bukkit.command.CommandSender;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
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
                    Class arrayedClass = Array.newInstance(convertClass, 0).getClass();
                    parameterTypes.put("[arguments]", arrayedClass);
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

    /**
     * Returns the posistions in the method parameters of each of the given argument accepting options
     *
     * @param method the command method
     * @param parser the parser with options set
     * @return map of option name->position in parameters
     */
    protected Map<String, Integer> getParameterPositions(Method method, OptionParser parser) throws CommandParseException
    {
        //NOTE: we can't get method parameter names due to the compiler not carrying them over
        //for arguments of the same type we will allocate them in alphabetical option name order

        Map<String, Integer> mapping = new HashMap<String, Integer>();

        Class[] types = method.getParameterTypes();

        //remove the first 2 parameters (must be OptionSet and ? extends CommandSender)
        if(types.length < 2 || !types[0].equals(OptionSet.class) || !CommandSender.class.isAssignableFrom(types[1])) {
            throw new CommandParseException("Invalid method signature for method: " + method.getName() + ", requires argument 1 to be an OptionSet and argument 2 to be CommandSender or a subclass");
        }

        //cut out the default parameters
        types = Arrays.copyOfRange(types, 2, types.length);

        //get the options defined in the parser
        Map<String, Class> options = getParameters(parser);

        //get the counts of each class for each
        HashMultiset<Class> originalCounts = HashMultiset.create(Arrays.asList(types));
        HashMultiset<Class> optionsCounts = HashMultiset.create(options.values());

        //if they don't match it won't work
        if(!originalCounts.equals(optionsCounts)) {
            throw new CommandParseException("Invalid method signature for method: " + method.getName() + ", not all defined options have a method argument");
        }

        //todo assign option name to parameter posistion

        return mapping;
    }

    /**
     * Checks if the given option posistions are valid for the method given
     *
     * @param method the method to check
     * @param posistions array of option names and their posistions
     * @param parser the parser with the options set
     * @param offset the offset in the methods parameters to start checking from (inclusive), 0 = all parameters
     * @return true if matches, false otherwise
     */
    protected boolean arePositionsCorrect(Method method, String[] posistions, OptionParser parser, int offset) throws CommandParseException
    {
        Map<String, Class> parameterClassMap = getParameters(parser);

        Class<?>[] parameters = method.getParameterTypes();

        //check if the offset exists
        if(parameters.length < offset)
            throw new CommandParseException("Method " + method + " does not have enough parameters");

        //remove all of the parameters we don't care about
        parameters = Arrays.copyOfRange(parameters, offset, parameters.length);

        //don't allow extra arguments not defined in the posistions array
        if(posistions.length != parameters.length)
            throw new CommandParseException("Method " + method + " option posistions length does not match method parameters");

        int numberOfOptions = posistions.length;

        if(numberOfOptions > parameterClassMap.size()) {
            throw new CommandParseException("Method " + method + " has more arguments than are defined in it's parser");
        }

        for(int i = 0; i < numberOfOptions; i++) {
            String optionName = posistions[i];

            if(!parameterClassMap.containsKey(optionName))
                throw new CommandParseException("Method " + method + " contains invalid option in posistions: " + optionName);

            Class<?> parameterClass = parameters[i];

            if(!parameterClass.isAssignableFrom(parameterClassMap.get(optionName)))
                throw new CommandParseException("Method " + method + " has wrong class type " + parameterClass.getName() + " for option: " + optionName);
        }

        return true;
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

        String[] optionPositions = annotation.optionOrder();

        Class[] parameters = method.getParameterTypes();

        if(parameters.length < 2)
            throw new CommandParseException("Method " + method.getName() + " needs at least 2 parameters, an OptionSet and a CommandSender");

        if(!parameters[0].equals(OptionSet.class))
            throw new CommandParseException("Method " + method.getName() + " does not have an OptionSet as parameter 1");

        Class[] allowedSenders = annotation.allowedSenders();
        Class<?> senderType = parameters[1];

        for(Class<?> senderClass : allowedSenders) {
            if(!senderType.isAssignableFrom(senderClass))
                throw new CommandParseException("Method " + method.getName() + " argument #2 is " + senderType.getName() + " but is not applicable to one of the restricted sender types: " + senderClass.getName());
        }

        //offset 2 because 1 = OptionSet and 2 = CommandSender (or subclasses)
        arePositionsCorrect(method, optionPositions, optionParser, 2);

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
