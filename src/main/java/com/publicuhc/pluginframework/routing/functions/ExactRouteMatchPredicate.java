package com.publicuhc.pluginframework.routing.functions;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.publicuhc.pluginframework.routing.CommandRoute;

import javax.annotation.Nullable;
import java.util.List;

public class ExactRouteMatchPredicate implements Predicate<CommandRoute>
{
    private final String route;

    /**
     * Filters based on if the routes startswith matches the given route exactly case insensitive
     * @param route the route to check against
     */
    public ExactRouteMatchPredicate(List<String> route)
    {
        this.route = Joiner.on(" ").join(route);
    }

    @Override
    public boolean apply(@Nullable CommandRoute commandRoute)
    {
        return commandRoute != null && Joiner.on(" ").join(commandRoute.getStartsWith()).equalsIgnoreCase(route);
    }
}
