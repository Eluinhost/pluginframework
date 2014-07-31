/*
 * UUIDFetcherTest.java
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
