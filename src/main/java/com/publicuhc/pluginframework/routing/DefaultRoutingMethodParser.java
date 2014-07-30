package com.publicuhc.pluginframework.routing;

import com.publicuhc.pluginframework.commands.annotation.CommandMethod;
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
     * @param method the original method
     * @param instance the instance to run on
     * @return the optionparser after being run through the method
     * @throws NoSuchMethodException if the method cannot be found
     */
    protected OptionParser getOptionsForMethod(Method method, Object instance) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method optionsMethod = instance.getClass().getMethod(method.getName(), OptionParser.class);

        //make a new parser and invoke the method with it
        OptionParser parser = new OptionParser();
        optionsMethod.invoke(instance, parser);

        return parser;
    }

    @Override
    public CommandRoute parseCommandMethodAnnotation(Method method, Object instance) throws CommandParseException
    {
        CommandMethod annotation = getAnnotation(method, CommandMethod.class);

        if(null == annotation)
            throw new AnnotationMissingException(method, CommandMethod.class);

        String options = annotation.options();

        OptionParser optionParser;
        
        //get our option parser for this command
        if(annotation.options().equals(CommandMethod.RUN_METHOD)) {
            try {
                optionParser = getOptionsForMethod(method, instance);
            } catch (NoSuchMethodException e) {
                throw new CommandParseException("No options given and no options method " + instance.getClass().getName() + "#" + method.getName() + " with argument OptionParser not found");
            } catch (Exception e) {
                throw new CommandParseException("Exception occured running the options method");
            }
        } else {
            //parse the annotation value in as our parser
            optionParser = new OptionParser(options);
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
