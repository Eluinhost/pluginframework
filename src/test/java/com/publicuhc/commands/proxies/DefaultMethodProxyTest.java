package com.publicuhc.commands.proxies;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
public class DefaultMethodProxyTest {

    private DefaultMethodProxy proxy;

    @Test
    public void testDoParamsMatch(){

        proxy.setPattern(Pattern.compile("[\\d]++"));
        assertFalse(proxy.doParamsMatch("e09=324"));
        assertTrue(proxy.doParamsMatch("3908420"));

        proxy.setPattern(Pattern.compile("[\\D]++"));
        assertFalse(proxy.doParamsMatch("3908420"));
        assertTrue(proxy.doParamsMatch("eff=gdk"));
    }

    @Before
    public void onStartUp(){
        proxy = new DefaultMethodProxy();
    }
}
