package com.publicuhc.commands.proxies;

import com.publicuhc.commands.requests.CommandRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
public class TabCompleteProxyTest {

    private TabCompleteProxy proxy;
    private TestTrigger trigger;


    @Test
    public void testTrigger() throws NoSuchMethodException, ProxyTriggerException {
        //set up the proxy
        proxy.setCommandMethod(trigger.getClass().getMethod("triggerTabComplete",CommandRequest.class));
        //make a fake request
        CommandRequest request = mock(CommandRequest.class);

        //trigger the proxy
        assertEquals(TestTrigger.args, proxy.trigger(request));
    }

    @Before
    public void onStartUp(){
        proxy = new TabCompleteProxy();
        trigger = new TestTrigger();
        proxy.setInstance(trigger);
    }
}
