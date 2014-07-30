package com.publicuhc.pluginframework.routing;

import com.google.inject.AbstractModule;

public class RoutingModule extends AbstractModule
{
    @Override
    protected void configure() {
        bind(Router.class).to(DefaultRouter.class);
        bind(RoutingMethodParser.class).to(DefaultRoutingMethodParser.class);
    }
}
