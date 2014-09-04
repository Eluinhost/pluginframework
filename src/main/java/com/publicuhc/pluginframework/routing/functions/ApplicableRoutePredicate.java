package com.publicuhc.pluginframework.routing.functions;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.publicuhc.pluginframework.routing.CommandRoute;

import javax.annotation.Nullable;

public class ApplicableRoutePredicate implements Predicate<CommandRoute>
{
    private final String actual;
    private final boolean subcommands;

    /**
     * Fetches all routes whose startsWith match the given args
     * @param args the arguments to match against
     * @param subcommands whether to show the route + subcommands (true) or route + parents (false)
     */
    public ApplicableRoutePredicate(String[] args, boolean subcommands)
    {
        actual = Joiner.on(" ").join(args).toLowerCase();
        this.subcommands = subcommands;
    }

    @Override
    public boolean apply(@Nullable CommandRoute commandRoute)
    {
        if(null == commandRoute) {
            return false;
        }

        String startsWith = Joiner.on(" ").join(commandRoute.getStartsWith());

        return subcommands ? startsWith.startsWith(actual) : actual.startsWith(startsWith);
    }
}
