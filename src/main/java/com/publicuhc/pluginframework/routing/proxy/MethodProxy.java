package com.publicuhc.pluginframework.routing.proxy;

import com.publicuhc.pluginframework.commands.routes.Route;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface MethodProxy {

    /**
     * @param instance the instance to run against
     */
    void setInstance(Object instance);

    /**
     * @return the instance to run with
     */
    Object getInstance();

    /**
     * @param method the method to run on command run
     */
    void setMethod(Method method);

    /**
     * @return the method to run the command on
     */
    public Method getMethod();

    /**
     * Invoke the method with the given arguments
     *
     * @param args the args to call the method with
     * @return the object returned by the method
     */
    public Object invoke(Object... args) throws InvocationTargetException, IllegalAccessException;
}
