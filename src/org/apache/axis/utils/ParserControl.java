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
 * ParserControl is used to control particular behaviour of the parser.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @since 4.0
 */
public interface ParserControl
{
    boolean isFinished( int lastOptionCode );
}
