package com.publicuhc.pluginframework.routing.help;

public class HelpMessage {
    final String option;
    final String description;

    public HelpMessage(String option, String description )
    {
        this.option = option;
        this.description = description;
    }

    @Override
    public boolean equals(Object that) {
        if (that == this)
            return true;
        if (that == null || !getClass().equals(that.getClass()))
            return false;

        HelpMessage other = (HelpMessage) that;
        return option.equals( other.option ) && description.equals( other.description );
    }

    @Override
    public int hashCode() {
        return option.hashCode() ^ description.hashCode();
    }
}
