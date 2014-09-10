/*
 * TestMethods.java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.publicuhc.pluginframework.routing.parser;

import com.publicuhc.pluginframework.routing.annotation.CommandMethod;
import com.publicuhc.pluginframework.routing.annotation.CommandOptions;
import com.publicuhc.pluginframework.routing.annotation.PermissionRestriction;
import com.publicuhc.pluginframework.routing.annotation.SenderRestriction;
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
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class TestMethods
{
    @CommandMethod("test")
    public void method(OptionSet set, CommandSender sender)
    {}

    @CommandMethod("test subcommand")
    public void methodSubcommand(OptionSet set, CommandSender sender)
    {}

    @CommandMethod("test")
    @CommandOptions
    public void methodWithOptions(OptionSet set, CommandSender sender)
    {}

    public void methodWithOptions(OptionDeclarer optionParser)
    {
        optionParser.accepts("a").withRequiredArg().required();
        optionParser.accepts("b").withOptionalArg();
    }

    @CommandMethod("test")
    @PermissionRestriction("TEST.PERMISSION")
    public void methodWithPermissions(OptionSet set, CommandSender sender)
    {}

    @CommandMethod("test")
    @SenderRestriction({ConsoleCommandSender.class, Player.class})
    public void methodWithChosenSenders(OptionSet set, CommandSender sender)
    {}

    @CommandMethod("test")
    @CommandOptions
    public void methodWithOptionsMissing(OptionSet set, CommandSender sender)
    {}

    @CommandMethod("test")
    @CommandOptions
    public void methodWithOptionsWithInvalidParam(OptionSet set, CommandSender sender)
    {}

    public void methodWithOptionsWithInvalidParam(String invalid)
    {}

    public void methodWithoutAnnotation(OptionSet set, CommandSender sender)
    {}

    @CommandMethod("test")
    public void methodWithInvalidParam1(OptionParser p, CommandSender sender)
    {}

    @CommandMethod("test")
    public void methodWithInvalidParam2(OptionSet set, Block block)
    {}

    @CommandMethod("test")
    public void methodWithUnmatchedSender(OptionSet set, Player sender)
    {}

    @CommandMethod("test")
    @SenderRestriction(Player.class)
    public void methodWithMatchedSender(OptionSet set, Player sender)
    {}

    @CommandMethod(value = "test", helpOption = "t")
    public void methodWithNonStandardHelp(OptionSet set, CommandSender sender)
    {}

    public void testOptionPosistions(OptionSet set, CommandSender sender, Location ex, String param, Double r, List players)
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
        return TestMethods.class.getDeclaredMethod("testOptionPosistions", OptionSet.class, CommandSender.class, Location.class, String.class, Double.class, List.class);
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
