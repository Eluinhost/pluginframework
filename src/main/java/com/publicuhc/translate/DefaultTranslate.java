package com.publicuhc.translate;

import com.google.inject.Inject;
import com.publicuhc.configuration.Config;
import com.publicuhc.translate.exceptions.LocaleNotFoundException;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class DefaultTranslate implements Translate {

    private final Config m_config;

    @Inject
    protected DefaultTranslate(Config config){
        m_config = config;
    }

    @Override
    public String translate(String key, String locale) {
        return translate(key, locale, new HashMap<String, String>());
    }

    @Override
    public String translate(String key, String locale, Map<String, String> vars) {
        String value = ""; //TODO read the key from the config file for the locale
        if(null == value){
            throw new LocaleNotFoundException();
        }
        for(String s : vars.keySet()){
            value = value.replaceAll("%"+s+"%", vars.get(s));
        }
        return value;
    }

    @Override
    public String translate(String key, String locale, String var, String value) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(var, value);
        return translate(key, locale, map);
    }

    @Override
    public String getLocaleForSender(CommandSender sender) {
        //TODO find the saved locale for the user
        //TODO return the default locale from the config file
        return null;
    }
}