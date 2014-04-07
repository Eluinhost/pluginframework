package com.publicuhc.commands.proxies;

import com.publicuhc.commands.requests.CommandRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class TabCompleteProxyTest {

    private TabCompleteProxy proxy;
    private TestTrigger trigger;
    private CommandRequest request;

    @Test
    public void testTrigger() throws NoSuchMethodException, ProxyTriggerException {
        //set up the proxy
        proxy.setCommandMethod(trigger.getClass().getMethod("triggerTabComplete",CommandRequest.class));

        //trigger the proxy
        assertEquals(TestTrigger.args, proxy.trigger(request));
    }

    @Test(expected = IllegalAccessError.class)
    public void testExceptionMethod() throws Exception {
        proxy.setCommandMethod(trigger.getClass().getMethod("triggerException", CommandRequest.class));

        try {
            proxy.trigger(request);
        }catch (ProxyTriggerException ex){
            throw ex.getActualException();
        }
    }

    @Before
    public void onStartUp(){
        proxy = new TabCompleteProxy();
        request = mock(CommandRequest.class);
        trigger = spy(new TestTrigger());
        PowerMockito.doThrow(new IllegalAccessError()).when(trigger).triggerException(request);
        when(trigger.triggerTabComplete(request)).thenCallRealMethod();

        proxy.setInstance(trigger);
    }
}
