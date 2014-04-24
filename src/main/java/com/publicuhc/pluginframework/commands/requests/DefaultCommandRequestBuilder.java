/*
 * DefaultCommandRequestBuilder.java
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

package com.publicuhc.pluginframework.commands.requests;

import com.google.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class DefaultCommandRequestBuilder implements CommandRequestBuilder {

    private CommandRequest m_request = new CommandRequest();
    private final Translate m_translate;

    @Inject
    public DefaultCommandRequestBuilder(Translate translate) {
        m_translate = translate;
    }

    @Override
    public CommandRequest build() {
        if (!m_request.isValid()) {
            throw new IllegalStateException("Command request state is invalid");
        }
        return m_request;
    }

    @Override
    public CommandRequestBuilder setCommand(Command command) {
        if (null == command) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        m_request.setCommand(command);
        return this;
    }

    protected Command getCommand() {
        return m_request.getCommand();
    }

    @Override
    public CommandRequestBuilder setCount(int count) {
        if( count < 0 ){
            throw new IllegalArgumentException("Count cannot be negative");
        }
        m_request.setCount(count);
        return this;
    }

    @Override
    public CommandRequestBuilder setLocale(String locale) {
        m_request.setLocale(locale);
        return this;
    }

    protected int getCount() {
        return m_request.getCount();
    }

    @Override
    public CommandRequestBuilder setArguments(List<String> arguments) {
        if (null == arguments) {
            throw new IllegalArgumentException("Argument list cannot be null");
        }
        m_request.setArgs(arguments);
        return this;
    }

    protected List<String> getArguments() {
        return m_request.getArgs();
    }

    @Override
    public CommandRequestBuilder setArguments(String[] arguments) {
        if (null == arguments) {
            throw new IllegalArgumentException("Argument list cannot be null");
        }
        return setArguments(Arrays.asList(arguments));
    }

    @Override
    public CommandRequestBuilder setSender(CommandSender sender) {
        if (null == sender) {
            throw new IllegalArgumentException("Sender cannot be null");
        }
        m_request.setCommandSender(sender);
        m_request.setLocale(m_translate.getLocaleForSender(sender));
        return this;
    }

    protected CommandSender getSender() {
        return m_request.getSender();
    }

    @Override
    public boolean isValid() {
        return m_request.isValid();
    }

    @Override
    public void clear() {
        m_request = new CommandRequest();
    }
}
