package com.publicuhc.pluginframework.translate;

import org.yaml.snakeyaml.Yaml;
import sun.util.ResourceBundleEnumeration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class YamlResourceBundle extends ResourceBundle {

    private ConcurrentMap<String, String> lookup = new ConcurrentHashMap<String, String>();

    public YamlResourceBundle(InputStream stream) throws IOException {
        Yaml loader = new Yaml();
        for (Object item : ((LinkedHashMap) loader.load(stream)).entrySet()) {
            Map.Entry<String, Object> itemEntry = (Map.Entry) item;
            getEntry(itemEntry.getKey(), itemEntry.getValue());

            Map.Entry<String, String> iEntry = (Map.Entry) item;
            lookup.put(iEntry.getKey(), iEntry.getValue());
        }
    }

    public YamlResourceBundle(InputStreamReader stream) throws IOException {
        Yaml loader = new Yaml();
        for (Object item : ((LinkedHashMap) loader.load(stream)).entrySet()) {
            Map.Entry<String, Object> itemEntry = (Map.Entry) item;
            getEntry(itemEntry.getKey(), itemEntry.getValue());

            Map.Entry<String, String> iEntry = (Map.Entry) item;
            lookup.put(iEntry.getKey(), iEntry.getValue());
        }
    }

    private void getEntry(String key, Object item) {
        if (item instanceof LinkedHashMap) {
            LinkedHashMap itemEntry = (LinkedHashMap) item;
            for (Object i : itemEntry.entrySet()) {
                Map.Entry<String, Object> ii = (Map.Entry) i;
                getEntry(key + "." + ii.getKey(), ii.getValue());
            }
        } else {
            lookup.put(key, (String) item);
        }

    }

    @Override
    protected Object handleGetObject(String key) {
        return lookup.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return new ResourceBundleEnumeration(lookup.keySet(), (parent != null) ? parent.getKeys() : null);
    }
}