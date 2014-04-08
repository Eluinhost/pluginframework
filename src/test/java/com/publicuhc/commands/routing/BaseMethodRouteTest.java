package com.publicuhc.commands.routing;

import com.publicuhc.commands.requests.SenderType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
public class BaseMethodRouteTest {

    private Pattern pattern;
    private SenderType[] allowed;
    private String perm;
    private String baseCommand;

    private BaseMethodRoute route;

    @Test
    public void testRoute() {
        assertThat(route.getRoute(), is(sameInstance(pattern)));
    }

    @Test
    public void testAllowedTypes() {
        assertThat(route.getAllowedTypes(), is(sameInstance(allowed)));
    }

    @Test
    public void testPermission() {
        assertThat(route.getPermission(), is(sameInstance(perm)));
    }

    @Test
    public void testBaseCommand() {
        assertThat(route.getBaseCommand(), is(sameInstance(baseCommand)));
    }

    @Before
    public void onStartUp() {
        pattern = mock(Pattern.class);
        allowed = new SenderType[]{SenderType.PLAYER, SenderType.CONSOLE};
        perm = "perms";
        baseCommand = "testcommand";

        route = new BaseMethodRoute(pattern, allowed, perm, baseCommand);
    }
}
