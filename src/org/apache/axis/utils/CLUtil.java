/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
// This file is pulled from package org.apache.avalon.excalibur.cli Excalibur
// version 4.1 (Jan 30, 2002).  Only the package name has been changed.
package org.apache.axis.utils;


/**
 * CLUtil offers basic utility operations for use both internal and external to package.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @since 4.0
 */
public final class CLUtil
{
    private final static int        MAX_DESCRIPTION_COLUMN_LENGTH = 60;

    /**
     * Format options into StringBuffer and return. This is typically used to
     * print "Usage" text in response to a "--help" or invalid option.
     *
     * @param options[] the option descriptors
     * @return the formatted description/help for options
     */
    public final static StringBuffer describeOptions( final CLOptionDescriptor[] options )
    {
        final StringBuffer sb = new StringBuffer();

        for( int i = 0; i < options.length; i++ )
        {
            final char ch = (char) options[i].getId();
            final String name = options[i].getName();
            String description = options[i].getDescription();
            int flags = options[i].getFlags();
            boolean argumentRequired =
                    ( (flags & CLOptionDescriptor.ARGUMENT_REQUIRED) ==
                      CLOptionDescriptor.ARGUMENT_REQUIRED);
            boolean twoArgumentsRequired =
                    ( (flags & CLOptionDescriptor.ARGUMENTS_REQUIRED_2) ==
                      CLOptionDescriptor.ARGUMENTS_REQUIRED_2);
            boolean needComma = false;
            if (twoArgumentsRequired)
                argumentRequired = true;

            sb.append('\t');

            if( Character.isLetter(ch) )
            {
                sb.append( "-" );
                sb.append( ch );
                needComma = true;
            }

            if( null != name )
            {
                if( needComma )
                {
                    sb.append( ", " );
                }

                sb.append( "--" );
                sb.append( name );
                if (argumentRequired)
                {
                    sb.append(" <argument>");
                }
                if (twoArgumentsRequired)
                {
                    sb.append("=<value>");
                }
                sb.append( JavaUtils.LS );
            }

            if( null != description )
            {
                while( description.length() > MAX_DESCRIPTION_COLUMN_LENGTH )
                {
                    final String descriptionPart =
                        description.substring( 0, MAX_DESCRIPTION_COLUMN_LENGTH );
                    description =
                        description.substring( MAX_DESCRIPTION_COLUMN_LENGTH );
                    sb.append( "\t\t" );
                    sb.append( descriptionPart );
                    sb.append( JavaUtils.LS );
                }

                sb.append( "\t\t" );
                sb.append( description );
                sb.append( JavaUtils.LS );
            }
        }
        return sb;
    }

    /**
     * Private Constructor so that no instance can ever be created.
     *
     */
    private CLUtil()
    {
    }
}
