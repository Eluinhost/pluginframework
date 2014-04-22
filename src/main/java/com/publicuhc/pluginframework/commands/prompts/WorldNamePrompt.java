/*
 * WorldNamePrompt.java
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
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;

public abstract class WorldNamePrompt extends FixedSetPrompt {

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {
        World world = Bukkit.getWorld(input);
        return world != null && isWorldValid(world);
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
        return acceptValidatedInput(conversationContext, Bukkit.getWorld(s));
    }

    /**
     * @param worlds the worlds to allow
     */
    protected WorldNamePrompt(String... worlds){
        super(worlds);
    }

    /**
     * @param conversationContext the context
     * @param world the accepted world
     * @return the next prompt
     */
    protected abstract Prompt acceptValidatedInput(ConversationContext conversationContext, World world);

    /**
     * @param world the world to check
     * @return true if world valid, false if not
     */
    protected boolean isWorldValid(World world){
        return true;
    }
}