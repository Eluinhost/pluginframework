package com.publicuhc.test.proxy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.junit.Assert.*;

import java.util.regex.Pattern;

@RunWith(PowerMockRunner.class)
public class ProxyTest {

    @Test
    public void testParamMatcher(){
        TestMethodProxy proxy = new TestMethodProxy();

        proxy.setPattern(Pattern.compile("[\\d]++"));

        assertTrue(proxy.doParamsMatch("09324"));
    }

    @Test
    public void testParamMatcherFail(){
        TestMethodProxy proxy = new TestMethodProxy();

        proxy.setPattern(Pattern.compile("[\\d]++"));

        assertFalse(proxy.doParamsMatch("e09=324"));
    }

    @Test
    public void testParamMatcherMulti(){
        TestMethodProxy proxy = new TestMethodProxy();

        proxy.setPattern(Pattern.compile("[\\d]++"));

        assertFalse(proxy.doParamsMatch("e09=324"));
        assertTrue(proxy.doParamsMatch("3908420"));
    }
}
