package com.publicuhc.pluginframework.routing.help;

import java.util.LinkedHashSet;
import java.util.Set;

public class HelpMessages {
    private final Set<HelpMessage> HelpMessages = new LinkedHashSet<HelpMessage>();

    public void add( String option, String description ) {
        add( new HelpMessage( option, description ) );
    }

    private void add( HelpMessage HelpMessage ) {
        HelpMessages.add( HelpMessage );
    }

    public String render() {
        StringBuilder buffer = new StringBuilder();

        for ( HelpMessage each : HelpMessages ) {
            buffer.append(each.option).append(' ').append(each.description).append("\n");
        }

        return buffer.toString();
    }
}
