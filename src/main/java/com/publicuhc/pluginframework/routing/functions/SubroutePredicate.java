package com.publicuhc.pluginframework.routing.functions;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.publicuhc.pluginframework.routing.CommandRoute;

import javax.annotation.Nullable;
import java.util.List;

public class SubroutePredicate implements Predicate<CommandRoute>
{
    private final String selectedRoute;
    private final int expectedLength;

    /**
     * Allows any route with startsWith().length = selectedRoute.size()  + 1
     * Also checks case insensitive the route startswith starts with the selected route.
     *
     * Ex.
     * selectedRoute = 'a', 'b'
     *
     * Would match: ['a', 'b', 'c'], ['a', 'b', 'd']
     * Wouldn't match: ['a', 'b'], ['a', 'b', 'c', 'd'], ['b', 'a']
     *
     * @param selectedRoute the current route to match
     */
    public SubroutePredicate(List<String> selectedRoute)
    {
        this.selectedRoute = Joiner.on(" ").join(selectedRoute).toLowerCase();
        expectedLength = selectedRoute.size() + 1;
    }

    @Override
    public boolean apply(@Nullable CommandRoute commandRoute)
    {
        if(commandRoute == null) {
            return false;
        }

        String[] startsWith = commandRoute.getStartsWith();

        if(startsWith.length == expectedLength) {
            String loweredStartsWith = Joiner.on(" ").join(startsWith).toLowerCase();
            if(loweredStartsWith.startsWith(selectedRoute)) {
                return true;
            }
        }
        return false;
    }
}
