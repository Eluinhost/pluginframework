package com.publicuhc.translate;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.publicuhc.configuration.Configurator;
import com.publicuhc.translate.exceptions.LocaleNotFoundError;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class DefaultTranslate implements Translate {

    private final Configurator m_configurator;
    private final String m_basePermission;

    @Inject
    protected DefaultTranslate(Configurator configurator, @Named("base_locale_permission") String basePermission) {
        m_configurator = configurator;
        m_basePermission = basePermission;
    }

    @Override
    public String translate(String key, String locale) {
        return translate(key, locale, new HashMap<String, String>());
    }

    @Override
    public String translate(String key, String locale, Map<String, String> vars) {
        FileConfiguration configuration = m_configurator.getConfig("translations/" + locale);

        if (null == configuration) {
            throw new LocaleNotFoundError();
        }

        String value = configuration.getString(key, "TRANSLATION KEY NOT SET FOR " + key);

        for (String s : vars.keySet()) {
            value = value.replaceAll("%" + s + "%", vars.get(s));
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
