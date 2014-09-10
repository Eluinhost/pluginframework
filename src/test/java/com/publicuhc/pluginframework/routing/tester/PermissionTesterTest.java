/*
 * PermissionTesterTest.java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.publicuhc.pluginframework.routing.tester;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class PermissionTesterTest
{
    @Test
    public void test_permission_tester()
    {
        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = new String[0];

        PermissionTester tester = new PermissionTester();

        //test no permissions set pass
        assertThat(tester.testCommand(command, sender, args)).isTrue();

        //test missing permission fail
        tester.add("TEST.PERMISSION.1");
        when(sender.hasPermission("TEST.PERMISSION.1")).thenReturn(false);
        assertThat(tester.testCommand(command, sender, args)).isFalse();

        //test permission pass
        when(sender.hasPermission("TEST.PERMISSION.1")).thenReturn(true);
        assertThat(tester.testCommand(command, sender, args)).isTrue();

        //add another permission
        tester.add("TEST.PERMISSION.2");

        //test matching all fail
        tester.setMatchingAll(true);
        when(sender.hasPermission("TEST.PERMISSION.2")).thenReturn(false);
        assertThat(tester.testCommand(command, sender, args)).isFalse();

        //test not matching all pass
        tester.setMatchingAll(false);
        assertThat(tester.testCommand(command, sender, args)).isTrue();

        //test matching all pass
        tester.setMatchingAll(true);
        when(sender.hasPermission("TEST.PERMISSION.2")).thenReturn(true);
        assertThat(tester.testCommand(command, sender, args)).isTrue();
    }
}
