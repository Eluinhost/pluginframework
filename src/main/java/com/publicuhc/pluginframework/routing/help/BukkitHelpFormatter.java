/*
 * BukkitHelpFormatter.java
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

package com.publicuhc.pluginframework.routing.help;

import joptsimple.HelpFormatter;
import joptsimple.OptionDescriptor;
import joptsimple.internal.Strings;

import java.util.*;

import static joptsimple.internal.Classes.shortNameOf;
import static joptsimple.internal.Strings.LINE_SEPARATOR;
import static joptsimple.internal.Strings.isNullOrEmpty;
import static joptsimple.internal.Strings.surround;

public class BukkitHelpFormatter implements HelpFormatter
{
    private HelpMessages nonOptions = new HelpMessages();
    private HelpMessages options = new HelpMessages();

    public String format( Map<String, ? extends OptionDescriptor> options ) {
        Comparator<OptionDescriptor> comparator =
                new Comparator<OptionDescriptor>() {
                    public int compare( OptionDescriptor first, OptionDescriptor second ) {
                        return first.options().iterator().next().compareTo( second.options().iterator().next() );
                    }
                };

        Set<OptionDescriptor> sorted = new TreeSet<OptionDescriptor>( comparator );
        sorted.addAll( options.values() );

        addRows( sorted );

        return formattedHelpOutput();
    }

    private String formattedHelpOutput() {
        StringBuilder formatted = new StringBuilder();
        String nonOptionDisplay = nonOptions.render();
        if ( !Strings.isNullOrEmpty(nonOptionDisplay) )
            formatted.append( nonOptionDisplay ).append( LINE_SEPARATOR );
        formatted.append(options.render());

        return formatted.toString();
    }

    private void addRows( Collection<? extends OptionDescriptor> options ) {
        addNonOptionsDescription( options );

        if ( options.isEmpty() )
            this.options.add( "No options specified", "" );
        else {
            addHeaders( options );
            addOptions( options );
        }
    }

    private void addNonOptionsDescription( Collection<? extends OptionDescriptor> options ) {
        OptionDescriptor nonOptions = findAndRemoveNonOptionsSpec( options );
        if ( shouldShowNonOptionArgumentDisplay( nonOptions ) ) {
            this.nonOptions.add( "Non-option arguments:", "" );
            this.nonOptions.add(createNonOptionArgumentsDisplay(nonOptions), "");
        }
    }

    private boolean shouldShowNonOptionArgumentDisplay( OptionDescriptor nonOptions ) {
        return !Strings.isNullOrEmpty( nonOptions.description() )
                || !Strings.isNullOrEmpty( nonOptions.argumentTypeIndicator() )
                || !Strings.isNullOrEmpty( nonOptions.argumentDescription() );
    }

    private String createNonOptionArgumentsDisplay(OptionDescriptor nonOptions) {
        StringBuilder buffer = new StringBuilder();
        maybeAppendOptionInfo( buffer, nonOptions );
        maybeAppendNonOptionsDescription( buffer, nonOptions );

        return buffer.toString();
    }

    private void maybeAppendNonOptionsDescription( StringBuilder buffer, OptionDescriptor nonOptions ) {
        buffer.append( buffer.length() > 0 && !Strings.isNullOrEmpty( nonOptions.description() ) ? " -- " : "" )
                .append( nonOptions.description() );
    }

    private OptionDescriptor findAndRemoveNonOptionsSpec( Collection<? extends OptionDescriptor> options ) {
        for ( Iterator<? extends OptionDescriptor> it = options.iterator(); it.hasNext(); ) {
            OptionDescriptor next = it.next();
            if ( next.representsNonOptions() ) {
                it.remove();
                return next;
            }
        }

        throw new AssertionError( "no non-options argument spec" );
    }

    private void addHeaders( Collection<? extends OptionDescriptor> options ) {
        if ( hasRequiredOption( options ) ) {
            this.options.add("Option (* = required)", "Description");
            this.options.add("---------------------", "-----------");
        } else {
            this.options.add("Option", "Description");
            this.options.add("------", "-----------");
        }
    }

    private boolean hasRequiredOption( Collection<? extends OptionDescriptor> options ) {
        for ( OptionDescriptor each : options ) {
            if ( each.isRequired() )
                return true;
        }

        return false;
    }

    private void addOptions( Collection<? extends OptionDescriptor> options ) {
        for ( OptionDescriptor each : options ) {
            if ( !each.representsNonOptions() )
                this.options.add( createOptionDisplay( each ), createDescriptionDisplay( each ) );
        }
    }

    private String createOptionDisplay( OptionDescriptor descriptor ) {
        StringBuilder buffer = new StringBuilder( descriptor.isRequired() ? "* " : "" );

        for ( Iterator<String> i = descriptor.options().iterator(); i.hasNext(); ) {
            String option = i.next();
            buffer.append( option.length() > 1 ? "--" : '-' );
            buffer.append( option );

            if ( i.hasNext() )
                buffer.append( ", " );
        }

        maybeAppendOptionInfo( buffer, descriptor );

        return buffer.toString();
    }

    private void maybeAppendOptionInfo( StringBuilder buffer, OptionDescriptor descriptor ) {
        String indicator = extractTypeIndicator( descriptor );
        String description = descriptor.argumentDescription();
        if ( indicator != null || !isNullOrEmpty( description ) )
            appendOptionHelp( buffer, indicator, description, descriptor.requiresArgument() );
    }

    private String extractTypeIndicator( OptionDescriptor descriptor ) {
        String indicator = descriptor.argumentTypeIndicator();

        if ( !isNullOrEmpty( indicator ) && !String.class.getName().equals( indicator ) )
            return shortNameOf( indicator );

        return null;
    }

    private void appendOptionHelp( StringBuilder buffer, String typeIndicator, String description, boolean required ) {
        if ( required )
            appendTypeIndicator( buffer, typeIndicator, description, '<', '>' );
        else
            appendTypeIndicator( buffer, typeIndicator, description, '[', ']' );
    }

    private void appendTypeIndicator( StringBuilder buffer, String typeIndicator, String description,
                                      char start, char end ) {
        buffer.append( ' ' ).append( start );
        if ( typeIndicator != null )
            buffer.append( typeIndicator );

        if ( !Strings.isNullOrEmpty( description ) ) {
            if ( typeIndicator != null )
                buffer.append( ": " );

            buffer.append( description );
        }

        buffer.append( end );
    }

    private String createDescriptionDisplay( OptionDescriptor descriptor ) {
        List<?> defaultValues = descriptor.defaultValues();
        if ( defaultValues.isEmpty() )
            return descriptor.description();

        String defaultValuesDisplay = createDefaultValuesDisplay( defaultValues );
        return ( descriptor.description() + ' ' + surround( "default: " + defaultValuesDisplay, '(', ')' ) ).trim();
    }

    private String createDefaultValuesDisplay( List<?> defaultValues ) {
        return defaultValues.size() == 1 ? defaultValues.get( 0 ).toString() : defaultValues.toString();
    }
}