package com.publicuhc.pluginframework.translate;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.lang.Validate;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

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
        if (format.equals("yml")) {
            String resourceName = toResourceName(bundleName, format);

            if (dataDir != null) {
                FileInputStream fis = null;
                InputStreamReader reader = null;

                try {
                    File file = new File(dataDir, resourceName);
                    if (file.isFile()) {
                        fis = new FileInputStream(file);
                        reader = new InputStreamReader(fis, "UTF-8");
                        bundle = new YamlResourceBundle(reader);
                    }
                } finally {
                    if (reader != null)
                        reader.close();
                    if (fis != null)
                        fis.close();
                }
            }

            if (bundle == null) {
                InputStream stream = null;
                if (reload) {
                    URL url = loader.getResource(resourceName);
                    if (url != null) {
                        URLConnection connection = url.openConnection();
                        if (connection != null) {
                            connection.setUseCaches(false);
                            stream = connection.getInputStream();
                        }
                    }
                } else {
                    stream = loader.getResourceAsStream(resourceName);
                }

                if (stream != null) {
                    BufferedInputStream bis = null;
                    try {
                        bis = new BufferedInputStream(stream);
                        bundle = new YamlResourceBundle(bis);
                    } finally {
                        if(bis != null) {
                            bis.close();
                        }
                    }
                }
            }
        } else {
            bundle = super.newBundle(bundleName, locale, format, loader, reload);
        }
        return bundle;
    }

}
