package com.publicuhc;

import java.util.regex.Pattern;

public interface Router {

    /**
     * Register the a path to a command
     * @param pattern the pattern to match
     * @param proxy the command to run
     */
    void registerRoute(Pattern pattern , CommandProxy proxy);

    /**
     * @return the route that matches the string given
     */
    CommandProxy getCommand(String command);
}
