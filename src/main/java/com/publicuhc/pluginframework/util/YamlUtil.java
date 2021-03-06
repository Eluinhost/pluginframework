/*
 * YamlUtil.java
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

package com.publicuhc.pluginframework.util;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class YamlUtil
{
    /**
     * Attempts to pull a Yaml file from the JAR
     *
     * @param path the path to check for
     * @param loader the classloader to look in
     * @return optional. Present if found, null if not
     *
     * @throws IOException
     * @throws InvalidConfigurationException if the file can't be parsed
     */
    public static Optional<YamlConfiguration> loadYamlFromJAR(String path, ClassLoader loader) throws IOException, InvalidConfigurationException
    {
        Validate.notNull(path, "Path to file cannot be null");

        URL url = loader.getResource(path);

        if(url == null) {
            return Optional.absent();
        }

        StringBuilder builder = new StringBuilder();
        CharStreams.copy(Resources.newReaderSupplier(url, Charsets.UTF_8), builder);

        return Optional.of(loadYamlFromString(builder.toString()));
    }

    /**
     * Loads a Yaml file from the given string contents
     *
     * @param contents contents of the raw yaml
     * @return the parsed file
     * @throws InvalidConfigurationException if the contents are invalid
     */
    public static YamlConfiguration loadYamlFromString(String contents) throws InvalidConfigurationException
    {
        Validate.notNull(contents, "Contents of the Yaml file cannot be null");

        YamlConfiguration configuration = new YamlConfiguration();
        configuration.loadFromString(contents);
        return configuration;
    }

    /**
     * Loads the yaml from the harddrive
     *
     * @param path the path inside the directory
     * @param dir the directory to check in
     * @return optional, present if file found, absent if not
     * @throws IOException
     * @throws InvalidConfigurationException if file failed to parse
     */
    public static Optional<YamlConfiguration> loadYamlFromDir(String path, File dir) throws IOException, InvalidConfigurationException
    {
        Validate.notNull(path, "Path to file cannot be null");

        File file = new File(dir, path);

        if(!file.isFile()) {
            return Optional.absent();
        }

        String fileContent = Files.toString(file, Charsets.UTF_8);

        return Optional.of(loadYamlFromString(fileContent));
    }

    /**
     * Save the config file to the path within the given directory
     *
     * @param configuration file to save
     * @param dataDir the directory to save in
     * @param path the path to the file to save as
     */
    public static void saveConfiguration(FileConfiguration configuration, File dataDir, String path)
    {
        Validate.notNull(configuration);
        Validate.notNull(dataDir);
        Validate.notNull(path);

        try {
            configuration.save(new File(dataDir, path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the given file from the harddrive with it's defaults set to the JAR version (and saved to the HDD)
     *
     * @param path the path to the file to load
     * @param loader the classloader to load from
     * @param dataDir the directory to save/load from
     *
     * @return optional fileconfiguration. Absent if jar AND hdd version don't exist
     * @throws IOException
     * @throws InvalidConfigurationException if HDD or JAR file failed to parse
     */
    public static Optional<FileConfiguration> loadConfigWithDefaults(String path, ClassLoader loader, File dataDir) throws IOException, InvalidConfigurationException
    {
        Optional<YamlConfiguration> jarOptional = YamlUtil.loadYamlFromJAR(path, loader);

        Optional<YamlConfiguration> hardDriveOptional = YamlUtil.loadYamlFromDir(path, dataDir);

        if(!hardDriveOptional.isPresent() && !jarOptional.isPresent()) {
            return Optional.absent();
        }

        YamlConfiguration hardDrive = hardDriveOptional.or(new YamlConfiguration());

        if(jarOptional.isPresent()) {
            YamlConfiguration jar = jarOptional.get();
            hardDrive.setDefaults(jar);
            hardDrive.options().copyDefaults(true);
        }

        YamlUtil.saveConfiguration(hardDrive, dataDir, path);

        return Optional.of((FileConfiguration) hardDrive);
    }
}
