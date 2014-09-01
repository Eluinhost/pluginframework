package com.publicuhc.pluginframework.translate;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.lang.Validate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class YamlControl extends ResourceBundle.Control
{
    private final File dataFolder;

    @Inject
    public YamlControl(@Named("dataFolder") File file)
    {
        dataFolder = file;
    }

    @Override
    public List<String> getFormats(String base)
    {
        Validate.notNull(base);
        return Arrays.asList("yml");
    }

    @Override
    public ResourceBundle newBundle(String base, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException
    {
        Validate.notNull(base);
        Validate.notNull(locale);
        Validate.notNull(format);
        Validate.notNull(loader);

        String bundleName = toBundleName(base, locale);
        String resourceName = toResourceName(bundleName, format);
        File file = new File(dataFolder, resourceName);

        InputStream is = null;
        ResourceBundle bundle = null;

        try {
            if(file.isFile()) {
                is = new FileInputStream(file);
            } else {
                if(reload) {
                    URL url = loader.getResource("translations/" + resourceName);
                    if(url != null) {
                        URLConnection connection = url.openConnection();
                        if(connection != null) {
                            connection.setUseCaches(false);
                            is = connection.getInputStream();
                        }
                    }
                } else {
                    is = loader.getResourceAsStream("translations/" + resourceName);
                }
            }
            bundle = new YamlResourceBundle(is);

        } finally {
            if(is != null) {
                is.close();
            }
        }
        return bundle;
    }

}
