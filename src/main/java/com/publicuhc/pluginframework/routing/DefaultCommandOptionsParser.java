package com.publicuhc.pluginframework.routing;

import com.publicuhc.pluginframework.routing.exception.OptionMissingException;
import org.bukkit.craftbukkit.libs.joptsimple.OptionParser;
import org.bukkit.craftbukkit.libs.joptsimple.OptionSet;

import java.util.ArrayList;
import java.util.List;

public class DefaultCommandOptionsParser implements CommandOptionsParser {

    private final OptionParser parser;
    private final String[] requiredOptions;

    public DefaultCommandOptionsParser(OptionParser parser, String[] required)
    {
        this.parser = parser;
        this.requiredOptions = required;
    }

    public String[] getRequiredOptions()
    {
        return requiredOptions;
    }

    public OptionParser getParser()
    {
        return parser;
    }

    /**
     * Proxy for {@link org.bukkit.craftbukkit.libs.joptsimple.OptionParser#parse(String...)} but also checks for all options set
     *
     * @param args the args to parse
     * @return the parsed optionset
     * @throws org.bukkit.craftbukkit.libs.joptsimple.OptionException when failing parsing the args or required args are not present
     * @throws com.publicuhc.pluginframework.routing.exception.OptionMissingException when a required option is missing
     */
    public OptionSet parse(String... args)
    {
        OptionSet set = parser.parse(args);

        List<String> missing = new ArrayList<String>();
        for (String req : requiredOptions) {
            if(!set.has(req)) {
                missing.add(req);
            }
        }

        if(!missing.isEmpty())
            throw new OptionMissingException(missing);

        return set;
    }
}
