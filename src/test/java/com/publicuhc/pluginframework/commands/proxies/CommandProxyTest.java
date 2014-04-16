/*
 * CommandProxyTest.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of PluginFramework.
 *
 * PluginFramework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PluginFramework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PluginFramework.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.pluginframework.commands.proxies;

import com.publicuhc.pluginframework.commands.requests.CommandRequest;
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
        } catch (ProxyTriggerException ex) {
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
