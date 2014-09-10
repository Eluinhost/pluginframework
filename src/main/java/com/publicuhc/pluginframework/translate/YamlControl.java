/*
 * YamlControl.java
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

package com.publicuhc.pluginframework.translate;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.publicuhc.pluginframework.util.YamlUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YamlControl extends ResourceBundle.Control {

    private final File dataDir;

    @Inject
    public YamlControl(@Named("dataFolder") File dataDir)
    {
        this.dataDir = dataDir;
    }

    @Override
    public List<String> getFormats(String baseName)
    {
        Validate.notNull(baseName);
        return Arrays.asList("yml", "java.properties");
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException
    {
        Validate.notNull(baseName);
        Validate.notNull(locale);
        Validate.notNull(format);
        Validate.notNull(loader);

        String bundleName = toBundleName(baseName, locale);

        //we only know how to handle yml, if its a properties send to parent method
        if (!format.equals("yml")) {
            return super.newBundle(bundleName, locale, format, loader, reload);
        }

        //get the actual name of the file
        String resourceName = toResourceName(bundleName, format);

        try {
            Optional<FileConfiguration> file = YamlUtil.loadConfigWithDefaults(resourceName, loader, dataDir);

            if(!file.isPresent()) {
                //say we couldn't find it
                return null;
            }

            return new YamlResourceBundle(file.get());

        } catch(InvalidConfigurationException e) {
            throw new InvalidPropertiesFormatException(e);
        }
    }
}
