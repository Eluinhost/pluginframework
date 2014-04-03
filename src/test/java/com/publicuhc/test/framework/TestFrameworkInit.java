package com.publicuhc.test.framework;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.publicuhc.Framework;
import com.publicuhc.commands.CommandModule;
import com.publicuhc.commands.routing.Router;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class TestFrameworkInit {

    @Test
    public void testFrameworkInit(){
        Injector i = Guice.createInjector(new CommandModule());
        Framework framework = i.getInstance(Framework.class);
        assertNotNull(framework.getRouter());
    }
}
