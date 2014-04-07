package com.publicuhc.commands.proxies;

import com.publicuhc.commands.requests.CommandRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
public class CommandProxyTest {

    private CommandProxy proxy;
    private TestTrigger trigger;

    @Test
    public void testTrigger() throws NoSuchMethodException, ProxyTriggerException {
        //set up the proxy
        proxy.setCommandMethod(trigger.getClass().getMethod("triggerCommandMethod",CommandRequest.class));
        //make a fake request
        CommandRequest request = mock(CommandRequest.class);

        //trigger the proxy
        proxy.trigger(request);

        verify(trigger, times(1)).triggerCommandMethod(request);
    }

    @Before
    public void onStartUp() {
        proxy = new CommandProxy();
        trigger = mock(TestTrigger.class);
        proxy.setInstance(trigger);
    }
}
