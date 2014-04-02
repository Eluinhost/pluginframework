package com.publicuhc;

public interface CommandMapper {

    /**
     * Register the object for commands
     * @param klass the object to use
     */
    void registerCommands(Class klass);
}
