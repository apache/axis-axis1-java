/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

package org.apache.axis.client;

import java.util.* ;
import java.net.*;
import java.io.IOException;
import org.apache.axis.MessageContext;
import org.apache.axis.AxisFault;
import org.apache.axis.AxisEngine;
import org.apache.axis.Handler;

public class Transport {

    /**
     * Synonyms for MessageContext userid / password.
     */
    public static String USER = MessageContext.USERID;
    public static String PASSWORD = MessageContext.PASSWORD;

    /**
     * Transport Chain Name - so users can change the default.
     */
    public String transportName = null ;

    /**
     * Transport URL, if any.
     */
    public String url = null;

    /**
     * Set up any transport-specific derived properties in the message context.
     * @param context the context to set up
     * @param message the client service instance
     * @param engine the engine containing the registries
     * @throws AxisFault if service cannot be found
     */
    public final void setupMessageContext(MessageContext context,
                                          ServiceClient message,
                                          AxisEngine engine)
        throws AxisFault
    {
        if (url != null)
            context.setProperty(MessageContext.TRANS_URL, url);

        if (transportName != null)
            context.setTransportName(transportName);

        setupMessageContextImpl(context, message, engine);
    }

    /**
     * Set up any transport-specific derived properties in the message context.
     * @param context the context to set up
     * @param message the client service instance
     * @param engine the engine containing the registries
     * @throws AxisFault if service cannot be found
     */
    public void setupMessageContextImpl(MessageContext context,
                                        ServiceClient message,
                                        AxisEngine engine)
        throws AxisFault
    {
        // Default impl does nothing
    }

    /**
     * Allow the transport to grab any transport-specific stuff it might
     * want from a returned MessageContext
     */
    public void processReturnedMessageContext(MessageContext context)
    {
        // Default impl does nothing
    }

    /**
     * Sets the transport chain name - to override the default.
     * @param name the name of the transport chain to use
     */
    public void setTransportName(String name) {
        transportName = name ;
    }

    /**
     * Returns the name of the transport chain to use
     * @return the transport chain name (or null if the default chain)
     */
    public String getTransportName() {
        return( transportName );
    }

    /**
     * Get the transport-specific URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the transport-specific URL
     */
    public void setUrl(String url) {
        this.url = url;
    }
}


