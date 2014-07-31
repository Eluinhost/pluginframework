package com.publicuhc.pluginframework.routing.parser;

import com.publicuhc.pluginframework.routing.CommandMethod;
import com.publicuhc.pluginframework.routing.CommandRequest;
import com.publicuhc.pluginframework.routing.CommandRoute;
import com.publicuhc.pluginframework.routing.DefaultCommandRoute;
import com.publicuhc.pluginframework.routing.exception.AnnotationMissingException;
import com.publicuhc.pluginframework.routing.exception.CommandParseException;
import com.publicuhc.pluginframework.routing.proxy.MethodProxy;
import com.publicuhc.pluginframework.routing.proxy.ReflectionMethodProxy;
import org.bukkit.craftbukkit.libs.joptsimple.OptionParser;

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
    protected CommandOptionsParser getOptionsForMethod(Method method, Object instance) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method optionsMethod = instance.getClass().getMethod(method.getName(), OptionParser.class);

        if(!(optionsMethod.getReturnType().isAssignableFrom(String[].class)))
            throw new NoSuchMethodException("Options method does not return a String[]");

        //make a new parser and invoke the method with it
        OptionParser parser = new OptionParser();
        String[] required = (String[]) optionsMethod.invoke(instance, parser);

        return new DefaultCommandOptionsParser(parser, required);
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

        CommandOptionsParser optionParser;

        //get our option parser for this command
        if(annotation.options()) {
            try {
                optionParser = getOptionsForMethod(method, instance);
            } catch(Exception e) {
                throw new CommandParseException("Exception occured when trying to run the options method for " + method.getName(), e);
            }
        } else {
            optionParser = new DefaultCommandOptionsParser();
        }

        MethodProxy proxy = new ReflectionMethodProxy(instance, method);

        return new DefaultCommandRoute(annotation.command(), proxy, optionParser);
    }

    @Override
    public boolean hasCommandMethodAnnotation(Method method)
    {
        return getAnnotation(method, CommandMethod.class) != null;
    }
}
