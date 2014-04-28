/*
 * CommandModule.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of PluginFramework.
 *
 * PluginFramework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PluginFramework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PluginFramework.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.pluginframework.commands;

import com.google.inject.AbstractModule;
import com.publicuhc.pluginframework.commands.requests.CommandRequestBuilder;
import com.publicuhc.pluginframework.commands.requests.DefaultCommandRequestBuilder;
import com.publicuhc.pluginframework.commands.routes.DefaultRouteBuilder;
import com.publicuhc.pluginframework.commands.routes.RouteBuilder;
import com.publicuhc.pluginframework.commands.routing.*;

public class CommandModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Router.class).to(DefaultRouter.class);
        bind(RouteBuilder.class).to(DefaultRouteBuilder.class);
        bind(CommandRequestBuilder.class).to(DefaultCommandRequestBuilder.class);
        bind(MethodChecker.class).to(DefaultMethodChecker.class);
    }
}
