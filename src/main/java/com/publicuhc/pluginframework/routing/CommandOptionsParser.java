package com.publicuhc.pluginframework.routing;

import org.bukkit.craftbukkit.libs.joptsimple.OptionParser;
import org.bukkit.craftbukkit.libs.joptsimple.OptionSet;

public interface CommandOptionsParser
{

    public String[] getRequiredOptions();

    public OptionParser getParser();

    /**
     * Proxy for {@link org.bukkit.craftbukkit.libs.joptsimple.OptionParser#parse(String...)} but also checks for all options set
     *
     * @param args the args to parse
     * @return the parsed optionset
     * @throws org.bukkit.craftbukkit.libs.joptsimple.OptionException                 when failing parsing the args or required args are not present
     * @throws com.publicuhc.pluginframework.routing.exception.OptionMissingException when a required option is missing
     */
    public OptionSet parse(String... args);
}
