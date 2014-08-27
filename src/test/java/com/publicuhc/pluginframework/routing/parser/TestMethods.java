/*
 * TestMethods.java
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

package com.publicuhc.pluginframework.routing.parser;

import com.publicuhc.pluginframework.routing.CommandMethod;
import com.publicuhc.pluginframework.routing.converters.LocationValueConverter;
import com.publicuhc.pluginframework.routing.converters.OnlinePlayerValueConverter;
import joptsimple.OptionDeclarer;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

@SuppressWarnings("UnusedDeclaration")
public class TestMethods
{
    @CommandMethod(command = "test")
    public void method(OptionSet set, CommandSender sender)
    {}

    @CommandMethod(command = "test subcommand")
    public void methodSubcommand(OptionSet set, CommandSender sender)
    {}

    @CommandMethod(command = "test", options = true)
    public void methodWithOptions(OptionSet set, CommandSender sender)
    {}

    public void methodWithOptions(OptionDeclarer optionParser)
    {
        optionParser.accepts("a").withRequiredArg().required();
        optionParser.accepts("b").withOptionalArg();
    }

    @CommandMethod(command = "test", permission = "TEST.PERMISSION")
    public void methodWithPermissions(OptionSet set, CommandSender sender)
    {}

    @CommandMethod(command = "test", allowedSenders = {ConsoleCommandSender.class, Player.class})
    public void methodWithChosenSenders(OptionSet set, CommandSender sender)
    {}

    @CommandMethod(command = "test", options = true)
    public void methodWithOptionsMissing(OptionSet set, CommandSender sender)
    {}

    @CommandMethod(command = "test", options = true)
    public void methodWithOptionsWithInvalidParam(OptionSet set, CommandSender sender)
    {}

    public void methodWithOptionsWithInvalidParam(String invalid)
    {}

    public void methodWithoutAnnotation(OptionSet set, CommandSender sender)
    {}

    @CommandMethod(command = "test")
    public void methodWithInvalidParam1(OptionParser p, CommandSender sender)
    {}

    @CommandMethod(command = "test")
    public void methodWithInvalidParam2(OptionSet set, Block block)
    {}

    @CommandMethod(command = "test")
    public void methodWithUnmatchedSender(OptionSet set, Player sender)
    {}

    @CommandMethod(command = "test", allowedSenders = Player.class)
    public void methodWithMatchedSender(OptionSet set, Player sender)
    {}

    @CommandMethod(command = "test", helpOption = "t")
    public void methodWithNonStandardHelp(OptionSet set, CommandSender sender)
    {}

    public void testOptionPosistions(OptionSet set, CommandSender sender, Location ex, String param, Double r, Player[][] players)
    {}

    public Method getMethod() throws NoSuchMethodException
    {
        return TestMethods.class.getDeclaredMethod("method", OptionSet.class, CommandSender.class);
    }

    public Method getMethodSubcommand() throws NoSuchMethodException
    {
        return TestMethods.class.getDeclaredMethod("methodSubcommand", OptionSet.class, CommandSender.class);
    }

    public Method getMethodWithOptions() throws NoSuchMethodException
    {
        return TestMethods.class.getDeclaredMethod("methodWithOptions", OptionSet.class, CommandSender.class);
    }

    public Method getMethodWithPermission() throws NoSuchMethodException
    {
        return TestMethods.class.getDeclaredMethod("methodWithPermissions", OptionSet.class, CommandSender.class);
    }

    public Method getMethodWithChosenSenders() throws NoSuchMethodException
    {
        return TestMethods.class.getDeclaredMethod("methodWithChosenSenders", OptionSet.class, CommandSender.class);
    }

    public Method getMethodWithOptionsMissing() throws NoSuchMethodException
    {
        return TestMethods.class.getDeclaredMethod("methodWithOptionsMissing", OptionSet.class, CommandSender.class);
    }

    public Method getMethodWithOptionsWithInvalidParam() throws NoSuchMethodException
    {
        return TestMethods.class.getDeclaredMethod("methodWithOptionsWithInvalidParam", OptionSet.class, CommandSender.class);
    }

    public Method getMethodWithoutAnnotation() throws NoSuchMethodException
    {
        return TestMethods.class.getDeclaredMethod("methodWithoutAnnotation", OptionSet.class, CommandSender.class);
    }

    public Method getMethodWithInvalidParam1() throws NoSuchMethodException
    {
        return TestMethods.class.getDeclaredMethod("methodWithInvalidParam1", OptionParser.class, CommandSender.class);
    }

    public Method getMethodWithInvalidParam2() throws NoSuchMethodException
    {
        return TestMethods.class.getDeclaredMethod("methodWithInvalidParam2", OptionSet.class, Block.class);
    }

    public Method getMethodWithUnmatchedSender() throws NoSuchMethodException
    {
        return TestMethods.class.getDeclaredMethod("methodWithUnmatchedSender", OptionSet.class, Player.class);
    }

    public Method getMethodWithMatchedSender() throws NoSuchMethodException
    {
        return TestMethods.class.getDeclaredMethod("methodWithMatchedSender", OptionSet.class, Player.class);
    }

    public Method getMethodWithNonStandardHelp() throws NoSuchMethodException
    {
        return TestMethods.class.getDeclaredMethod("methodWithNonStandardHelp", OptionSet.class, CommandSender.class);
    }

    public Method getTestOptionsPosistions() throws NoSuchMethodException
    {
        return TestMethods.class.getDeclaredMethod("testOptionPosistions", OptionSet.class, CommandSender.class, Location.class, String.class, Double.class, Player[][].class);
    }

    public OptionParser getParserForOptionPosistionMethod()
    {
        OptionParser p = new OptionParser();
        p.accepts("l")
                .withOptionalArg()
                .withValuesConvertedBy(new LocationValueConverter());
        p.accepts("string")
                .withRequiredArg();
        p.accepts("radius")
                .withRequiredArg()
                .ofType(Double.class);
        p.nonOptions().withValuesConvertedBy(new OnlinePlayerValueConverter(true));
        return p;
    }
}
