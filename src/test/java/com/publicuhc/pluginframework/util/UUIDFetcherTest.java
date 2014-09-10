/*
 * UUIDFetcherTest.java
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

package com.publicuhc.pluginframework.util;

import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
public class UUIDFetcherTest {

    @Test
    public void testUUIDFetch() throws ParseException, InterruptedException, IOException {
        UUID uuid = UUIDFetcher.getUUIDOf("ghowden");

        assertThat(uuid, is(equalTo(UUID.fromString("048fa310-30de-44fe-9f5e-c7443e91ad46"))));
        assertThat(uuid.toString(), is(equalTo("048fa310-30de-44fe-9f5e-c7443e91ad46")));
    }

    @Test
    public void testUUIDFetchMulti() throws ParseException, InterruptedException, IOException {
        UUIDFetcher fetcher = new UUIDFetcher("ghowden", "Eluinhost");

        Map<String, UUID> uuids = fetcher.call();

        Map<String, UUID> expected = new HashMap<String, UUID>();
        expected.put("ghowden", UUID.fromString("048fa310-30de-44fe-9f5e-c7443e91ad46"));
        expected.put("Eluinhost", UUID.fromString("6ac803fd-132f-4540-a741-cb18ffeed8ce"));

        assertThat(uuids, is(equalTo(expected)));
    }
}
