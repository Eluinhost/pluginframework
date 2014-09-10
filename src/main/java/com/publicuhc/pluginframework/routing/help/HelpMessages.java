/*
 * HelpMessages.java
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

package com.publicuhc.pluginframework.routing.help;

import java.util.LinkedHashSet;
import java.util.Set;

public class HelpMessages {
    private final Set<HelpMessage> HelpMessages = new LinkedHashSet<HelpMessage>();

    public void add( String option, String description ) {
        add( new HelpMessage( option, description ) );
    }

    private void add( HelpMessage HelpMessage ) {
        HelpMessages.add( HelpMessage );
    }

    public String render() {
        StringBuilder buffer = new StringBuilder();

        for ( HelpMessage each : HelpMessages ) {
            buffer.append(each.option).append(' ').append(each.description).append("\n");
        }

        return buffer.toString();
    }
}
