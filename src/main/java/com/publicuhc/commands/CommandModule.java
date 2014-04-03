package com.publicuhc.commands;

import com.google.inject.AbstractModule;
import com.publicuhc.commands.requests.CommandRequestBuilder;
import com.publicuhc.commands.requests.DefaultCommandRequestBuilder;
import com.publicuhc.commands.routing.DefaultRouter;
import com.publicuhc.commands.routing.Router;

public class CommandModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Router.class).to(DefaultRouter.class);
        bind(CommandRequestBuilder.class).to(DefaultCommandRequestBuilder.class);
    }
}
