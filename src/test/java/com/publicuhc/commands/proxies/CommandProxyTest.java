package com.publicuhc.commands.proxies;

import com.publicuhc.commands.requests.CommandRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
public class CommandProxyTest {

    private CommandProxy proxy;
    private TestTrigger trigger;
    private CommandRequest request;

    @Test
    public void testTrigger() throws NoSuchMethodException, ProxyTriggerException {
        //set up the proxy
        proxy.setCommandMethod(trigger.getClass().getMethod("triggerCommandMethod", CommandRequest.class));

        //trigger the proxy
        proxy.trigger(request);

        verify(trigger, times(1)).triggerCommandMethod(request);
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
    public void onStartUp() {
        proxy = new CommandProxy();
        trigger = mock(TestTrigger.class);
        request = mock(CommandRequest.class);
        PowerMockito.doThrow(new IllegalAccessError()).when(trigger).triggerException(request);
        proxy.setInstance(trigger);
    }
}
