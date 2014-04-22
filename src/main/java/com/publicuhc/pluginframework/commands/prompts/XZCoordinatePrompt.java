/*
 * XZCoordinatePrompt.java
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
import org.bukkit.conversations.ValidatingPrompt;

public abstract class XZCoordinatePrompt extends ValidatingPrompt {

    @Override
    protected boolean isInputValid(ConversationContext conversationContext, String s) {
        String[] parts = s.split(",");
        if (parts.length != 2) {
            return false;
        }
        try {
            //noinspection ResultOfMethodCallIgnored
            Double.valueOf(parts[0]);
            //noinspection ResultOfMethodCallIgnored
            Double.valueOf(parts[1]);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
        String[] parts = s.split(",");
        double x = Double.valueOf(parts[0]);
        double z = Double.valueOf(parts[1]);
        return acceptValidatedInput(conversationContext, new SimplePair<Double, Double>(x, z));
    }

    /**
     * Accept the validatied input
     *
     * @param conversationContext the context
     * @param coords              the coordinates supplied
     * @return next prompt
     */
    protected abstract Prompt acceptValidatedInput(ConversationContext conversationContext, SimplePair<Double, Double> coords);

}