/*
 * SenderType.java
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

package com.publicuhc.commands.requests;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

public enum SenderType {
    COMMAND_BLOCK(BlockCommandSender.class),
    REMOTE_CONSOLE(RemoteConsoleCommandSender.class),
    CONSOLE(ConsoleCommandSender.class),
    PLAYER(Player.class),
    OTHER(Void.class);

    private final Class m_clazz;

    /**
     * @param clazz the class representing
     */
    SenderType(Class clazz) {
        m_clazz = clazz;
    }

    /**
     * Returns the enum value for the sender
     *
     * @param sender the sender to parse
     * @return the SenderType or null if not known
     */
    public static SenderType getFromCommandSender(CommandSender sender) {
        for (SenderType type : SenderType.values()) {
            if (type.getClassType().isAssignableFrom(sender.getClass())) {
                return type;
            }
        }
        return OTHER;
    }

    /**
     * @return the class for this enum
     */
    private Class getClassType() {
        return m_clazz;
    }
}
