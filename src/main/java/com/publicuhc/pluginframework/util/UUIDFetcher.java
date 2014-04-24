/*
 * UUIDFetcher.java
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

package com.publicuhc.pluginframework.util;

import com.google.common.collect.ImmutableList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

public class UUIDFetcher implements Callable<Map<String, UUID>> {

    private static final double QUERIES_PER_REQUEST = 100;
    private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";

    private final JSONParser m_jsonParser = new JSONParser();
    private final List<String> m_names;

    public UUIDFetcher(String... names) {
        this.m_names = ImmutableList.copyOf(names);
    }

    public Map<String, UUID> call() throws IOException, ParseException, InterruptedException {
        Map<String, UUID> playerMap = new HashMap<String, UUID>();

        int requests = (int) Math.ceil(m_names.size() / QUERIES_PER_REQUEST);
        for (int i = 0; i < requests; i++) {
            HttpURLConnection connection = getConnection();

            String query = JSONArray.toJSONString(m_names.subList(i * 100, Math.min((i + 1) * 100, m_names.size())));
            writeQuery(connection, query);

            JSONArray parsedArray = (JSONArray) m_jsonParser.parse(new InputStreamReader(connection.getInputStream()));

            for (Object player : parsedArray) {
                JSONObject jsonPlayer = (JSONObject) player;
                String id = (String) jsonPlayer.get("id");
                String name = (String) jsonPlayer.get("name");

                UUID playerID = UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" +id.substring(20, 32));
                playerMap.put(name, playerID);
            }
            if (i != requests - 1) {
                Thread.sleep(100L);
            }
        }
        return playerMap;
    }

    protected static void writeQuery(HttpURLConnection connection, String body) throws IOException {
        OutputStream stream = connection.getOutputStream();
        stream.write(body.getBytes());
        stream.flush();
        stream.close();
    }

    protected static HttpURLConnection getConnection() throws IOException {
        URL url = new URL(PROFILE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        return connection;
    }

    public static UUID getUUIDOf(String name) throws ParseException, InterruptedException, IOException {
        return new UUIDFetcher(name).call().get(name);
    }
}
