package com.publicuhc.commands.proxies;

import org.bukkit.command.Command;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractMethodProxy implements MethodProxy {

    private Pattern m_pattern;
    private Method m_method;
    private Command m_command;
    private Object m_instance;
    private Matcher m_matcher;

    @Override
    public void setPattern(Pattern pattern) {
        m_pattern = pattern;
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
    public boolean doParamsMatch(String params) {
        if(m_matcher == null){
            m_matcher = m_pattern.matcher(params);
        }else {
            m_matcher.reset(params);
        }
        return m_matcher.matches();
    }
}
