package com.publicuhc.pluginframework.routing;

import com.google.inject.AbstractModule;
import com.publicuhc.pluginframework.routing.parser.DefaultRoutingMethodParser;
import com.publicuhc.pluginframework.routing.parser.RoutingMethodParser;

public class RoutingModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(Router.class).to(DefaultRouter.class);
        bind(RoutingMethodParser.class).to(DefaultRoutingMethodParser.class);
    }
}
