package com.publicuhc.pluginframework;

import org.mockito.internal.stubbing.defaultanswers.ReturnsEmptyValues;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class WithSelfAnswer implements Answer
{
    private final Answer delegate = new ReturnsEmptyValues();
    private final Class<?> klazz;

    public WithSelfAnswer(Class<?> klazz) {
        this.klazz = klazz;
    }

    public Object answer(InvocationOnMock invocation) throws Throwable {
        Class<?> returnType = invocation.getMethod().getReturnType();
        if (returnType == klazz) {
            return invocation.getMock();
        } else {
            return delegate.answer(invocation);
        }
    }
}
