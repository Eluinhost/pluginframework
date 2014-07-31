package com.publicuhc.pluginframework.routing.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionMethodProxy implements MethodProxy
{

    private Object instance;
    private Method method;

    /**
     * Setup a method proxy that will call the given method on the instance given
     *
     * @param instance the instance to call on
     * @param method   the method to call
     */
    public ReflectionMethodProxy(Object instance, Method method)
    {
        this.instance = instance;
        this.method = method;
    }


    @Override
    public void setInstance(Object instance)
    {
        this.instance = instance;
    }

    @Override
    public Object getInstance()
    {
        return this.instance;
    }

    @Override
    public Object invoke(Object... args) throws InvocationTargetException, IllegalAccessException
    {
        return method.invoke(instance, args);
    }
}
