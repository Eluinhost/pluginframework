package com.publicuhc.pluginframework.translate;

import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class YamlResourceBundle extends ResourceBundle {

    private FileConfiguration cache = null;

    public YamlResourceBundle(InputStream stream)
    {
        try {
            cache = getYamlConfig(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getCached()
    {
        return cache;
    }

    @Override
    public Enumeration<String> getKeys()
    {
        return Collections.enumeration(cache.getKeys(true));
    }

    @Override
    protected Object handleGetObject(String key)
    {
        return cache != null ? cache.get(key) : null;
    }

    protected FileConfiguration getYamlConfig(InputStream stream) throws IOException, InvalidConfigurationException
    {
        YamlConfiguration yamlConfig = new YamlConfiguration();

        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;

        try {
            reader = new InputStreamReader(stream, "UTF-8");
            bufferedReader = new BufferedReader(reader);
            StrBuilder builder = new StrBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.appendln(line);
            }
            yamlConfig.loadFromString(builder.toString());
        } finally {
            if (bufferedReader != null)
                bufferedReader.close();
            if (reader != null)
                reader.close();
        }
        return yamlConfig;
    }
}