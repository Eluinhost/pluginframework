/*
 * MethodHandleMethodProxy.java
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

package com.publicuhc.pluginframework.routing.proxy;

import com.google.common.collect.ObjectArrays;

import java.lang.invoke.MethodHandle;

public class MethodHandleMethodProxy<T> implements MethodProxy
{

    private T instance;
    private MethodHandle method;

    /**
     * Setup a method proxy that will call the given method on the instance given
     *
     * @param instance the instance to call on
     * @param method   the method to call
     */
    public MethodHandleMethodProxy(T instance, MethodHandle method)
    {
        this.instance = instance;
        this.method = method;
    }

    @Override
    public void setInstance(Object instance)
    {
        this.instance = (T) instance;
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
