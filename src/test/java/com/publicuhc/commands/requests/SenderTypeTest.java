package com.publicuhc.commands.requests;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
public class SenderTypeTest {

    @Test
    public void testGetFromCommandSender() {
        Player player = mock(Player.class);
        ConsoleCommandSender console = mock(ConsoleCommandSender.class);
        BlockCommandSender commandblock = mock(BlockCommandSender.class);
        RemoteConsoleCommandSender remote = mock(RemoteConsoleCommandSender.class);

        assertThat(SenderType.getFromCommandSender(player), is(SenderType.PLAYER));
        assertThat(SenderType.getFromCommandSender(console), is(SenderType.CONSOLE));
        assertThat(SenderType.getFromCommandSender(commandblock), is(SenderType.COMMAND_BLOCK));
        assertThat(SenderType.getFromCommandSender(remote), is(SenderType.REMOTE_CONSOLE));

        CommandSender other = mock(CommandSender.class);
        assertThat(SenderType.getFromCommandSender(other), is(SenderType.OTHER));
    }

}
