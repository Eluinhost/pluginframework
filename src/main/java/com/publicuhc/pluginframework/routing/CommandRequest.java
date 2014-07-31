/*
 * CommandRequest.java
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

package com.publicuhc.pluginframework.routing;

import com.publicuhc.pluginframework.util.UUIDFetcher;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.OptionSet;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CommandRequest {

    private List<String> m_args;
    private CommandSender m_sender;
    private Command m_command;
    private String m_locale;
    private OptionSet m_optionSet;

    public static UUID INVALID_ID = new UUID(0L, 0L);

    public CommandRequest(Command command, CommandSender sender, OptionSet set, String locale)
    {
        m_command = command;
        m_sender = sender;
        m_args = set.nonOptionArguments();
        m_optionSet = set;
        m_locale = locale;
    }

    public OptionSet getOptions()
    {
        return m_optionSet;
    }

    /**
     * @return Get the locale for this request
     */
    public String getLocale() {
        return m_locale;
    }

    /**
     * Remove the first element of the arguments
     */
    public void removeFirstArg() {
        if (!m_args.isEmpty()) {
            m_args.remove(0);
        }
    }

    /**
     * @return unmodifiable list of the arguments supplied
     */
    public List<String> getArgs() {
        return null == m_args ? null : Collections.unmodifiableList(m_args);
    }

    /**
     * @return the sender involved
     */
    public CommandSender getSender() {
        return m_sender;
    }

    /**
     * @return null if no args left or the arguement if exists
     */
    public String getFirstArg() {
        if (m_args.isEmpty()) {
            return null;
        }
        return m_args.get(0);
    }

    /**
     * @return the last arg or null if not exist
     */
    public String getLastArg() {
        if (m_args.isEmpty()) {
            return null;
        }
        return m_args.get(m_args.size() - 1);
    }

    /**
     * @return the commands name
     */
    public Command getCommand() {
        return m_command;
    }

    /**
     * @return the type of the sender
     */
    public SenderType getSenderType() {
        return SenderType.getFromCommandSender(m_sender);
    }

    /**
     * @param index the index to look for
     * @return true if arg is an int, false otherwise
     */
    public boolean isArgInt(int index) {
        if (!isArgPresent(index)) {
            throw new IllegalArgumentException("Index must be within the size of the argument list");
        }
        try {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(m_args.get(index));
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    /**
     * Get the int at the specified index
     *
     * @param index the index to look for
     * @return -1 if not an int, int value otherwise
     */
    public int getInt(int index) {
        if (!isArgPresent(index)) {
            throw new IllegalArgumentException("Index must be within the size of the argument list");
        }
        int returnValue = -1;
        try {
            returnValue = Integer.parseInt(m_args.get(index));
        } catch (NumberFormatException ignored) {
        }
        return returnValue;
    }

    /**
     * @param index the index to look for
     * @return null if not valid, world if valid
     */
    public World getWorld(int index) {
        if (!isArgPresent(index)) {
            throw new IllegalArgumentException("Index must be within the size of the argument list");
        }
        return Bukkit.getWorld(m_args.get(index));
    }

    /**
     * @param message message to pass on to the sender
     */
    public void sendMessage(String message) {
        m_sender.sendMessage(message);
    }

    /**
     * @param index the index to look for
     * @return the argument
     */
    public String getArg(int index) {
        if (!isArgPresent(index)) {
            throw new IllegalArgumentException("Index must be within the size of the argument list");
        }
        return m_args.get(index);
    }

    /**
     * @param index the index to look for
     * @return true if within list bounds, false otherwise
     */
    public boolean isArgPresent(int index) {
        return index > -1 && index < m_args.size();
    }

    /**
     * @param index the index to look for
     * @return player or null of not exists
     */
    public Player getPlayer(int index) {
        if (!isArgPresent(index)) {
            throw new IllegalArgumentException("Index must be within the size of the argument list");
        }
        return Bukkit.getPlayer(m_args.get(index));
    }

    /**
     * Return the UUID for the player given, looked up on Mojang servers
     * @param index the index to look for
     * @return the UUID or INVALID_ID constant if error/not found
     */
    public UUID getPlayerUUID(int index) {
        if (!isArgPresent(index)) {
            throw new IllegalArgumentException("Index must be within the size of the argument list");
        }
        UUID playerID;
        try {
            playerID = UUIDFetcher.getUUIDOf(m_args.get(index));
        } catch (@SuppressWarnings("OverlyBroadCatchBlock") Exception ignored) {
            playerID = INVALID_ID;
        }
        return playerID;
    }

    /**
     * @param index the index to look for
     * @return true if arg is an number, false otherwise
     */
    public boolean isArgNumber(int index) {
        if (!isArgPresent(index)) {
            throw new IllegalArgumentException("Index must be within the size of the argument list");
        }
        return NumberUtils.isNumber(m_args.get(index));
    }

    /**
     * Get the Number at the specified index
     *
     * @param index the index to look for
     * @return number
     * @throws java.lang.NumberFormatException if cannot be converted
     */
    public Number getNumber(int index) {
        if (!isArgPresent(index)) {
            throw new IllegalArgumentException("Index must be within the size of the argument list");
        }
        return NumberUtils.createNumber(m_args.get(index));
    }

    /**
     * Is the index a boolean value, 'true', 'on' or 'yes' count as true and 'false', 'off' or 'no' count as false, case insensitive
     *
     * @param index the index to look for
     * @return true if boolean false otherwise
     */
    public boolean isArgBoolean(int index) {
        if (!isArgPresent(index)) {
            throw new IllegalArgumentException("Index must be within the size of the argument list");
        }
        return BooleanUtils.toBooleanObject(m_args.get(index)) != null;
    }

    /**
     * @param index the index to look for
     * @return the boolean value at that index, true if true/yes/on, false otherwise
     */
    public boolean getBoolean(int index) {
        if (!isArgPresent(index)) {
            throw new IllegalArgumentException("Index must be within the size of the argument list");
        }
        return BooleanUtils.toBoolean(m_args.get(index));
    }
}
