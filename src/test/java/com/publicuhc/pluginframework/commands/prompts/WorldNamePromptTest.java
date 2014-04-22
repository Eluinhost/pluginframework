/*
 * WorldNamePromptTest.java
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

package com.publicuhc.pluginframework.commands.prompts;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class WorldNamePromptTest {

    @Test
    public void testIsValid() {
        mockStatic(Bukkit.class);
        World valid = mock(World.class);
        when(Bukkit.getWorld("testvalid")).thenReturn(valid);
        when(Bukkit.getWorld("invalid")).thenReturn(null);

        WorldNamePrompt prompt = new WorldNamePrompt("testvalid") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, World world) {
                return null;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return null;
            }
        };

        assertTrue(prompt.isInputValid(mock(ConversationContext.class), "testvalid"));
        assertFalse(prompt.isInputValid(mock(ConversationContext.class), "invalid"));
    }

}
