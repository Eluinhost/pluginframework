package com.publicuhc.test.proxy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.junit.Assert.*;

import java.util.regex.Pattern;

@RunWith(PowerMockRunner.class)
public class RouteMatchingTest {

    @Test
    public void testSuccessfulPatternMatch(){
        TestMethodProxy proxy = new TestMethodProxy();

        proxy.setPattern(Pattern.compile("[\\d]++"));

        assertTrue(proxy.doParamsMatch("09324"));
    }

    @Test
    public void testPatternMatchFail(){
        TestMethodProxy proxy = new TestMethodProxy();

        proxy.setPattern(Pattern.compile("[\\d]++"));

        assertFalse(proxy.doParamsMatch("e09=324"));
    }

    @Test
    public void testMatcherReuse(){
        TestMethodProxy proxy = new TestMethodProxy();

        proxy.setPattern(Pattern.compile("[\\d]++"));

        assertFalse(proxy.doParamsMatch("e09=324"));
        assertTrue(proxy.doParamsMatch("3908420"));
    }
}
