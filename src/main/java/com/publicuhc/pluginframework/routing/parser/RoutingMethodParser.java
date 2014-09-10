/*
 * RoutingMethodParser.java
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
import com.publicuhc.pluginframework.routing.exception.CommandParseException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class RoutingMethodParser
{

    /**
     * @param annotation the annotation to look for
     * @return annotation if found, null otherwise
     */
    public static <T extends Annotation> T getAnnotation(Method method, Class<T> annotation)
    {
        return method.getAnnotation(annotation);
    }

    /**
     * @param method   the method to parse
     * @param instance the instance to use
     * @return the proxy built from the given method
     * @throws com.publicuhc.pluginframework.routing.exception.CommandParseException if errors were thrown during parse
     */
    public abstract CommandRoute parseCommandMethodAnnotation(Method method, Object instance) throws CommandParseException;

    /**
     * Check if the given method has the command method annotation
     *
     * @param method the method to check
     */
    public abstract boolean hasCommandMethodAnnotation(Method method);
}