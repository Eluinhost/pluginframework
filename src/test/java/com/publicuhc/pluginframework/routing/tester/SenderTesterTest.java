/*
 * SenderTesterTest.java
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

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
public class SenderTesterTest
{
    @Test
    public void test_sender_tester()
    {
        Command command = mock(Command.class);
        CommandSender commandSender = mock(CommandSender.class);
        Player player = mock(Player.class);
        BlockCommandSender bSender = mock(BlockCommandSender.class);
        String[] args = new String[0];

        SenderTester tester = new SenderTester();

        //test no senders specified - fail
        assertThat(tester.testCommand(command, commandSender, args)).isFalse();

        //test with same class
        tester.add(CommandSender.class);
        assertThat(tester.testCommand(command, commandSender, args)).isTrue();

        tester.clear();

        tester.add(Player.class);
        assertThat(tester.testCommand(command, player, args)).isTrue();

        //test with invalid class
        assertThat(tester.testCommand(command, bSender, args)).isFalse();

        //test mutliclass
        tester.add(BlockCommandSender.class);
        assertThat(tester.testCommand(command, bSender, args)).isTrue();
        assertThat(tester.testCommand(command, player, args)).isTrue();

        //test inheritance
        tester.clear();
        tester.add(CommandSender.class);
        assertThat(tester.testCommand(command, player, args)).isTrue();
        assertThat(tester.testCommand(command, bSender, args)).isTrue();
    }
}
