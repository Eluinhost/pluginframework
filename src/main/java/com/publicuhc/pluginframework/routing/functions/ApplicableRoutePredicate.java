package com.publicuhc.pluginframework.routing.functions;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.publicuhc.pluginframework.routing.CommandRoute;

import javax.annotation.Nullable;

public class ApplicableRoutePredicate implements Predicate<CommandRoute>
{
    private final String actual;

    /**
     * Fetches all routes whose startsWith match the given args
     * @param args the arguments to match against
     */
    public ApplicableRoutePredicate(String[] args)
    {
        actual = Joiner.on(" ").join(args).toLowerCase();
    }

    @Override
    public boolean apply(@Nullable CommandRoute commandRoute)
    {
        if(null == commandRoute) {
            return false;
        }

        String startsWith = Joiner.on(" ").join(commandRoute.getStartsWith());

        return actual.startsWith(startsWith);
    }
}
