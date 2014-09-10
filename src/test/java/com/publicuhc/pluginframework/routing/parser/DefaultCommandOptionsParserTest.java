/*
 * DefaultCommandOptionsParserTest.java
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

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(PowerMockRunner.class)
public class DefaultCommandOptionsParserTest
{
    private OptionParser optionsParser;

    @Before
    public void onStartup()
    {
        OptionParser parser = new OptionParser();
        parser.accepts("a").withRequiredArg().required(); // must have an arg if supplied
        parser.accepts("b").withOptionalArg().required(); // optionally have an arg is supplied
        parser.accepts("c").withOptionalArg().required();                   // no arg if supplied
        parser.accepts("d").withRequiredArg(); // must have an arg if supplied
        parser.accepts("e").withOptionalArg(); // optionally have an arg is supplied
        parser.accepts("f");                   // no arg if supplied

        optionsParser = parser;
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

        assertThat(set.nonOptionArguments()).hasSize(2);
        assertThat(set.nonOptionArguments().get(0)).isEqualTo("extra");
        assertThat(set.nonOptionArguments().get(1)).isEqualTo("args");
    }

    @Test(expected = OptionException.class)
    public void test_parsing_with_missing_a_argument()
    {
        optionsParser.parse("-b=b", "-c", "-d", "d", "extra", "args");
    }

    @Test(expected = OptionException.class)
    public void test_parsing_with_missing_b_argument()
    {
        optionsParser.parse("--a=a", "-c", "-d", "d", "extra", "args");
    }

    @Test(expected = OptionException.class)
    public void test_parsing_with_missing_c_argument()
    {
        optionsParser.parse("--a=a", "-b=b", "-d", "d", "extra", "args");
    }
}
