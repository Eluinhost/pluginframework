/*
 * DefaultRoutingMethodParser.java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.publicuhc.pluginframework.routing.parser;

import com.publicuhc.pluginframework.routing.CommandRoute;
import com.publicuhc.pluginframework.routing.DefaultCommandRoute;
import com.publicuhc.pluginframework.routing.annotation.CommandMethod;
import com.publicuhc.pluginframework.routing.annotation.CommandOptions;
import com.publicuhc.pluginframework.routing.annotation.PermissionRestriction;
import com.publicuhc.pluginframework.routing.annotation.SenderRestriction;
import com.publicuhc.pluginframework.routing.exception.AnnotationMissingException;
import com.publicuhc.pluginframework.routing.exception.CommandParseException;
import com.publicuhc.pluginframework.routing.help.BukkitHelpFormatter;
import com.publicuhc.pluginframework.routing.proxy.MethodProxy;
import com.publicuhc.pluginframework.routing.proxy.ReflectionMethodProxy;
import com.publicuhc.pluginframework.routing.tester.CommandTester;
import com.publicuhc.pluginframework.routing.tester.PermissionTester;
import com.publicuhc.pluginframework.routing.tester.SenderTester;
import joptsimple.*;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DefaultRoutingMethodParser extends RoutingMethodParser
{

    private final Field converterFieldArguments;
    private final Field separaterFieldArguments;

    /**
     * Used to create CommandRoute objects from a method with an @CommandMethod annotation
     */
    public DefaultRoutingMethodParser()
    {
        //setup the reflection to get the ValueConverter from the option specs
        Field argConverterField = null;
        Field argSeparaterField = null;
        try {
            //get the converter field and allow it to be accessed via reflection
            argConverterField = ArgumentAcceptingOptionSpec.class.getDeclaredField("converter");
            argConverterField.setAccessible(true);

            argSeparaterField = ArgumentAcceptingOptionSpec.class.getDeclaredField("valueSeparator");
            argSeparaterField.setAccessible(true);
        } catch(NoSuchFieldException e) {
            e.printStackTrace();
        }

        this.converterFieldArguments = argConverterField;
        this.separaterFieldArguments = argSeparaterField;
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
                parameterTypes.put("[arguments]", List.class);
            }

            //if its an an option with an argument we add it as it's option name
            if(optionSpec instanceof ArgumentAcceptingOptionSpec) {
                ArgumentAcceptingOptionSpec argspec = (ArgumentAcceptingOptionSpec) optionSpec;

                //only do it for options that accept arguments
                if(argspec.acceptsArguments()) {
                    try {
                        String separater = (String) separaterFieldArguments.get(argspec);
                        if(!separater.equals("\u0000")) {
                            parameterTypes.put(option.getKey(), List.class);
                        } else {
                            ValueConverter converter = (ValueConverter) converterFieldArguments.get(argspec);
                            Class convertClass = converter == null ? String.class : converter.valueType();
                            parameterTypes.put(option.getKey(), convertClass);
                        }
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

    /**
     * Checks if the given option posistions are valid for the method given
     *
     * @param method the method to check
     * @param posistions array of option names and their posistions
     * @param parser the parser with the options set
     * @param offset the offset in the methods parameters to start checking from (inclusive), 0 = all parameters
     * @throws com.publicuhc.pluginframework.routing.exception.CommandParseException when something isn't right
     */
    protected void checkPositionsCorrect(Method method, String[] posistions, OptionParser parser, int offset) throws CommandParseException
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
    }

    @Override
    public CommandRoute parseCommandMethodAnnotation(Method method, Object instance) throws CommandParseException
    {
        CommandMethod annotation = getAnnotation(method, CommandMethod.class);

        if(null == annotation)
            throw new AnnotationMissingException(method, CommandMethod.class);

        CommandOptions options = getAnnotation(method, CommandOptions.class);

        OptionParser optionParser;
        String[] optionPosistions;

        //if the annotation doesn't exist, use default parser
        if(null == options) {
            optionParser = new OptionParser();
            optionPosistions = new String[0];
        } else {
            //otherwise call the options method to get the parser
            try {
                optionParser = getOptionsForMethod(method, instance);
            } catch(Exception e) {
                throw new CommandParseException("Exception occured when trying to run the options method for " + method.getName(), e);
            }
            //set the options posistions
            optionPosistions = options.value();
        }

        //check the basics of the signature
        Class[] parameters = method.getParameterTypes();

        if(parameters.length < 2)
            throw new CommandParseException("Method " + method.getName() + " needs at least 2 parameters, an OptionSet and a CommandSender");

        if(!parameters[0].equals(OptionSet.class))
            throw new CommandParseException("Method " + method.getName() + " does not have an OptionSet as parameter 1");

        //get the sender restrictions applied to the method
        SenderRestriction senders = getAnnotation(method, SenderRestriction.class);
        SenderTester senderTester = new SenderTester();

        if(null == senders) {
            senderTester.add(CommandSender.class);
        } else {
            Collections.addAll(senderTester, senders.value());
        }

        //check the second parameter fit the sender restriction
        Class<?> senderType = parameters[1];
        if(!senderTester.isApplicable(senderType)) {
            throw new CommandParseException("Method " + method.getName() + " argument #2 is " + senderType.getName() + " but is not applicable to one of the restricted sender types");
        }

        //offset 2 because 1 = OptionSet and 2 = CommandSender (or subclasses)
        checkPositionsCorrect(method, optionPosistions, optionParser, 2);

        //add the help formatter and add the default help option
        optionParser.formatHelpWith(new BukkitHelpFormatter());
        OptionSpec helpSpec = optionParser.accepts(annotation.helpOption(), "Shows help").forHelp();

        //read the permissions from the annotation
        PermissionRestriction permissionRestriction = getAnnotation(method, PermissionRestriction.class);
        PermissionTester permissionTester = new PermissionTester();

        if(null != permissionRestriction) {
            Collections.addAll(permissionTester, permissionRestriction.value());
            permissionTester.setMatchingAll(permissionRestriction.needsAll());
        }

        //setup the restrictions for the route
        List<CommandTester> testers = new ArrayList<CommandTester>();
        testers.add(permissionTester);
        testers.add(senderTester);

        //setup the proxy and create the route
        MethodProxy proxy = new ReflectionMethodProxy(instance, method);
        return new DefaultCommandRoute(annotation.value(), proxy, optionParser, optionPosistions, helpSpec, testers);
    }

    @Override
    public boolean hasCommandMethodAnnotation(Method method)
    {
        return getAnnotation(method, CommandMethod.class) != null;
    }
}
