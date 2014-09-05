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

        ResourceBundle bundle = null;

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
