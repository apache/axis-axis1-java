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
 * Basic class describing an type of option.
 * Typically, one creates a static array of <code>CLOptionDescriptor</code>s,
 * and passes it to {@link CLArgsParser#CLArgsParser(String[], CLOptionDescriptor[])}.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @since 4.0
 */
public final class CLOptionDescriptor
{
    /** Flag to say that one argument is required */
    public static final int                  ARGUMENT_REQUIRED         = 1 << 1;
    /** Flag to say that the argument is optional */
    public static final int                  ARGUMENT_OPTIONAL         = 1 << 2;
    /** Flag to say this option does not take arguments */
    public static final int                  ARGUMENT_DISALLOWED       = 1 << 3;
    /** Flag to say this option requires 2 arguments */
    public static final int                  ARGUMENTS_REQUIRED_2      = 1 << 4;
    /** Flag to say this option may be repeated on the command line */
    public static final int                  DUPLICATES_ALLOWED        = 1 << 5;

    private final int                      m_id;
    private final int                      m_flags;
    private final String                   m_name;
    private final String                   m_description;
    private final int[]                    m_incompatible;

    /**
     * Constructor.
     *
     * @param name the name/long option
     * @param flags the flags
     * @param id the id/character option
     * @param description description of option usage
     */
    public CLOptionDescriptor( final String name,
                               final int flags,
                               final int id,
                               final String description )
    {
        this( name, flags, id, description,
            ((flags & CLOptionDescriptor.DUPLICATES_ALLOWED) > 0)
                ? new int[] {}
                : new int[] { id } );
    }

    /**
     * Constructor.
     *
     * @param name the name/long option
     * @param flags the flags
     * @param id the id/character option
     * @param description description of option usage
     */
    public CLOptionDescriptor( final String name,
                               final int flags,
                               final int id,
                               final String description,
                               final int[] incompatable )
    {
        m_id = id;
        m_name = name;
        m_flags = flags;
        m_description = description;
        m_incompatible = incompatable;
    }

    /**
     * @deprecated Use the correctly spelled {@link #getIncompatible} instead.
     */
    protected final int[] getIncompatble()
    {
        return getIncompatible();
    }

    protected final int[] getIncompatible()
    {
        return m_incompatible;
    }

    /**
     * Retrieve textual description.
     *
     * @return the description
     */
    public final String getDescription()
    {
        return m_description;
    }

    /**
     * Retrieve flags about option.
     * Flags include details such as whether it allows parameters etc.
     *
     * @return the flags
     */
    public final int getFlags()
    {
        return m_flags;
    }

    /**
     * Retrieve the id for option.
     * The id is also the character if using single character options.
     *
     * @return the id
     */
    public final int getId()
    {
        return m_id;
    }

    /**
     * Retrieve name of option which is also text for long option.
     *
     * @return name/long option
     */
    public final String getName()
    {
        return m_name;
    }

    /**
     * Convert to String.
     *
     * @return the converted value to string.
     */
    public final String toString()
    {
        return
            new StringBuffer()
            .append("[OptionDescriptor ").append(m_name)
            .append(", ").append(m_id).append(", ").append(m_flags)
            .append(", ").append(m_description).append(" ]").toString();
    }
}
