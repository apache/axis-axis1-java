/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.axis.components.net;

import org.apache.axis.AxisProperties;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import org.apache.commons.discovery.tools.SPInterface;

import java.lang.reflect.Constructor;
import java.util.Hashtable;

/**
 * Class SocketFactoryFactory
 *
 * @author
 * @version %I%, %G%
 */
public class SocketFactoryFactory {

    /** Field log           */
    protected static Log log =
            LogFactory.getLog(SocketFactoryFactory.class.getName());

    /** socket factory */
    private static SocketFactory theFactory = null;

    /** secure socket factory */
    private static SocketFactory theSecureFactory = null;

    private static final Class classes[] = new Class[] { Hashtable.class };
    
    /**
     * Returns a copy of the environment's default socket factory.
     *
     * @param attributes
     *
     * @return
     */
    public static synchronized SocketFactory getFactory(Hashtable attributes) {
        if (theFactory == null) {
            Object objects[] = new Object[] { attributes };

            theFactory = (SocketFactory)AxisProperties.newInstance(
                     new SPInterface(SocketFactory.class,
                                     "axis.socketFactory",
                                     classes,
                                     objects),
                     "org.apache.axis.components.net.DefaultSocketFactory");
        }
        return theFactory;
    }

    /**
     * Returns a copy of the environment's default secure socket factory.
     *
     * @param attributes
     *
     * @return
     */
    public static synchronized SocketFactory getSecureFactory(
            Hashtable attributes) {
        if (theSecureFactory == null) {
            Object objects[] = new Object[] { attributes };

            theSecureFactory = (SocketFactory)AxisProperties.newInstance(
                    new SPInterface(SocketFactory.class,
                                    "axis.socketSecureFactory",
                                    classes,
                                    objects),
                    "org.apache.axis.components.net.DefaultSecureSocketFactory");
        }
        return theSecureFactory;
    }
}
