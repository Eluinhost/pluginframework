/*
 * DefaultTranslate.java
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
