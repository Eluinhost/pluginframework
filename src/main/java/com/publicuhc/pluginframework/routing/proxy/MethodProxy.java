package com.publicuhc.pluginframework.routing.proxy;

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
     * @throws java.lang.Throwable any error caused during invocation
     */
    public Object invoke(Object... args) throws Throwable;
}
