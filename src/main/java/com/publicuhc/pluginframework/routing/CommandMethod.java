/*
 * CommandMethod.java
 *
 * Copyright (c) 2014. Graham Howden <graham_howden1 at yahoo.co.uk>.
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

package com.publicuhc.pluginframework.routing;

import org.bukkit.command.CommandSender;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface CommandMethod
{
    public static final String NO_PERMISSIONS = "NO PERMISSIONS REQUIRED";
    public static final String DEFAULT_HELP = "?";

    public String command();

    public boolean options() default false;

    public String permission() default NO_PERMISSIONS;

    public String helpOption() default DEFAULT_HELP;

    public Class<? extends CommandSender>[] allowedSenders() default CommandSender.class;
}