/*
 * DefaultCommandOptionsParserTest.java
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

import com.publicuhc.pluginframework.routing.exception.OptionMissingException;
import com.publicuhc.pluginframework.routing.parser.DefaultCommandOptionsParser;
import org.bukkit.craftbukkit.libs.joptsimple.OptionParser;
import org.bukkit.craftbukkit.libs.joptsimple.OptionSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(PowerMockRunner.class)
public class DefaultCommandOptionsParserTest
{
    private DefaultCommandOptionsParser optionsParser;

    @Before
    public void onStartup()
    {
        OptionParser parser = new OptionParser();
        parser.accepts("a").withRequiredArg(); // must have an arg if supplied
        parser.accepts("b").withOptionalArg(); // optionally have an arg is supplied
        parser.accepts("c");                   // no arg if supplied
        parser.accepts("d").withRequiredArg(); // must have an arg if supplied
        parser.accepts("e").withOptionalArg(); // optionally have an arg is supplied
        parser.accepts("f");                   // no arg if supplied

        //a b and c are required args, d e and f are optional
        String[] required = new String[]{"a", "b", "c"};

        optionsParser = new DefaultCommandOptionsParser(parser, required);
    }

    @Test
    public void test_parsing_with_valid_arguments()
    {
        OptionSet set = optionsParser.parse("--a=a", "-b=b", "-c", "-d", "d", "extra", "args");

        assertThat(set.has("a")).isTrue();
        assertThat(set.has("b")).isTrue();
        assertThat(set.has("c")).isTrue();
        assertThat(set.has("d")).isTrue();
        assertThat(set.has("e")).isFalse();
        assertThat(set.has("f")).isFalse();

        assertThat(set.hasArgument("a")).isTrue();
        assertThat(set.hasArgument("b")).isTrue();
        assertThat(set.hasArgument("c")).isFalse();
        assertThat(set.hasArgument("d")).isTrue();
        assertThat(set.hasArgument("e")).isFalse();
        assertThat(set.hasArgument("f")).isFalse();

        assertThat(set.valueOf("a")).isEqualTo("a");
        assertThat(set.valueOf("b")).isEqualTo("b");
        assertThat(set.valueOf("d")).isEqualTo("d");

        assertThat(set.nonOptionArguments()).containsExactly("extra", "args");
    }

    @Test(expected = OptionMissingException.class)
    public void test_parsing_with_missing_a_argument()
    {
        optionsParser.parse("-b=b", "-c", "-d", "d", "extra", "args");
    }

    @Test(expected = OptionMissingException.class)
    public void test_parsing_with_missing_b_argument()
    {
        optionsParser.parse("--a=a", "-c", "-d", "d", "extra", "args");
    }

    @Test(expected = OptionMissingException.class)
    public void test_parsing_with_missing_c_argument()
    {
        optionsParser.parse("--a=a", "-b=b", "-d", "d", "extra", "args");
    }
}
