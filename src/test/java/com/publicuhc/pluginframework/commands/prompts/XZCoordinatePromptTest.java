/*
 * XZCoordinatePromptTest.java
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

import com.publicuhc.pluginframework.util.SimplePair;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
public class XZCoordinatePromptTest {

    @Test
    public void testIsValid() {
        XZCoordinatePrompt prompt = new XZCoordinatePrompt() {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, SimplePair<Double, Double> coords) {
                return null;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return null;
            }
        };

        assertThat(prompt.isInputValid(mock(ConversationContext.class), "100,-200")).isTrue();
        assertThat(prompt.isInputValid(mock(ConversationContext.class), "dsklfj,as")).isFalse();
    }

}
