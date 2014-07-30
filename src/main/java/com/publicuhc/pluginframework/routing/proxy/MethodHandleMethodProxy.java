package com.publicuhc.pluginframework.routing.proxy;

import net.minecraft.util.com.google.common.collect.ObjectArrays;

import java.lang.invoke.MethodHandle;

public class MethodHandleMethodProxy<T> implements MethodProxy<T> {

    private T instance;
    private MethodHandle method;

    /**
     * Setup a method proxy that will call the given method on the instance given
     * @param instance the instance to call on
     * @param method the method to call
     */
    public MethodHandleMethodProxy(T instance, MethodHandle method)
    {
        this.instance = instance;
        this.method = method;
    }

    @Override
    public void setInstance(T instance)
    {
        this.instance = instance;
    }

    @Override
    public T getInstance()
    {
        return this.instance;
    }

    @Override
    public Object invoke(Object... args) throws Throwable
    {
        return method.invokeWithArguments(ObjectArrays.concat(instance, args));
    }
}
