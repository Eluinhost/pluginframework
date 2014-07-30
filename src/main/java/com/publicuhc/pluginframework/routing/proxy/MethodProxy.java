package com.publicuhc.pluginframework.routing.proxy;

import java.lang.reflect.InvocationTargetException;

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
     * Invoke the method with the given arguments
     *
     * @param args the args to call the method with
     * @return the object returned by the method
     */
    public Object invoke(Object... args) throws InvocationTargetException, IllegalAccessException;
}
