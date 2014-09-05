package com.publicuhc.pluginframework.translate;

import com.google.common.collect.Iterators;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Set;

public class YamlResourceBundle extends ResourceBundle {

    private FileConfiguration configuration;

    public YamlResourceBundle(FileConfiguration configuration) throws InvalidConfigurationException, IOException
    {
        Validate.notNull(configuration);
        this.configuration = configuration;
    }


    @Override
    public Enumeration<String> getKeys() {
        //get all of the keys we know about
        Set<String> configKeys = configuration.getKeys(true);

        //if we have a parent file add all of their keys too
        if(parent != null) {
            Iterators.addAll(configKeys, Iterators.forEnumeration(parent.getKeys()));
        }

        //return the list of keys
        return Collections.enumeration(configKeys);
    }

    @Override
    protected Object handleGetObject(String key) {
        //return the object if we have it
        return configuration.get(key);
    }
}