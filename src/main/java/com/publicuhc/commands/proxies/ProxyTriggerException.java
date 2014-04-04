package com.publicuhc.commands.proxies;

public class ProxyTriggerException extends Exception {
    private Exception m_actualException;

    public ProxyTriggerException(Exception ex) {
        m_actualException = ex;
    }

    public Exception getActualException() {
        return m_actualException;
    }
}
