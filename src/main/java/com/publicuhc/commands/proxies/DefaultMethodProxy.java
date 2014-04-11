/*
 * DefaultMethodProxy.java
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

package com.publicuhc.commands.proxies;

import com.publicuhc.commands.requests.SenderType;
import org.bukkit.command.Command;

import java.lang.reflect.Method;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultMethodProxy implements MethodProxy {

    private Pattern m_pattern;
    private Method m_method;
    private Command m_command;
    private Object m_instance;
    private Matcher m_matcher;
    private String m_permission;
    private SenderType[] m_allowedSenders;

    @Override
    public void setPattern(Pattern pattern) {
        m_pattern = pattern;
        m_matcher = null;
    }

    @Override
    public void setInstance(Object object) {
        m_instance = object;
    }

    @Override
    public void setCommandMethod(Method method) {
        m_method = method;
    }

    @Override
    public void setBaseCommand(Command command) {
        m_command = command;
    }

    @Override
    public Command getBaseCommand() {
        return m_command;
    }

    @Override
    public Pattern getPattern() {
        return m_pattern;
    }

    @Override
    public Object getInstance() {
        return m_instance;
    }

    @Override
    public Method getCommandMethod() {
        return m_method;
    }

    @Override
    public MatchResult paramsMatch(String params) {
        if (m_matcher == null) {
            m_matcher = m_pattern.matcher(params);
        } else {
            m_matcher.reset(params);
        }
        return m_matcher.matches() ? m_matcher.toMatchResult() : null;
    }

    public String getPermission() {
        return m_permission;
    }

    public void setPermission(String permission) {
        m_permission = permission;
    }

    public SenderType[] getAllowedSenders() {
        return m_allowedSenders;
    }

    public void setAllowedSenders(SenderType[] allowedSenders) {
        m_allowedSenders = allowedSenders;
    }
}
