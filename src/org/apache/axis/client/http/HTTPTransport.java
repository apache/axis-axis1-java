/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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
 *    Apache Software Foundation (http://www.apache.org/)."
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

package org.apache.axis.client.http;

import java.util.* ;
import org.apache.axis.* ;
import org.apache.axis.utils.Debug ;
import org.apache.axis.handlers.* ;
import org.apache.axis.registries.* ;
import org.apache.axis.client.ServiceClient;
import org.apache.axis.client.Transport;
import org.apache.axis.transport.http.HTTPConstants;

/**
 * Extends Transport by implementing the setupMessageContext function to
 * set HTTP-specific message context fields and transport chains.
 * May not even be necessary if we arrange things differently somehow.
 * Can hold state relating to URL properties.
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@allaire.com)
 */
public class HTTPTransport extends Transport
{
    /**
     * HTTP properties
     */
    static public String URL = MessageContext.TRANS_URL;
    static public String ACTION = HTTPConstants.MC_HTTP_SOAPACTION;
    
    private String url;
    private String action;
    
    public HTTPTransport () {
    }
    
    /**
     * helper constructor
     */
    public HTTPTransport (String url, String action)
    {
        this.url = url;
        this.action = action;
    }
    
    /**
     * Initialize the given MessageContext with the correct handlers and registries.
     */
    public void initMessageContext (MessageContext mc, ServiceClient serviceClient, Handler engine, boolean doLocal)
        throws AxisFault
    {
        DefaultServiceRegistry sr = (DefaultServiceRegistry)engine.getOption(Constants.SERVICE_REGISTRY);
        if ( sr == null || sr.find("HTTP.input") == null )
            mc.setProperty( MessageContext.TRANS_INPUT, "HTTPSender" );
        else
            mc.setProperty( MessageContext.TRANS_INPUT, "HTTP.input" );
        
        mc.setProperty(MessageContext.TRANS_OUTPUT, "HTTP.output" );
        
        /* If there is Input Transport Chain then default to HTTP. */
        /* In order for the client to override the transport chain */
        /* they should just set the TRANS_INPUT/OUTPUT fields in   */
        /* the msgContext.                                         */
        /***********************************************************/
        if ( mc.getProperty( MessageContext.TRANS_INPUT ) == null )
            mc.setProperty( MessageContext.TRANS_INPUT, "HTTPSender" );
        
        if ( mc.getProperty( MessageContext.TRANS_OUTPUT ) == null )
            mc.setProperty( MessageContext.TRANS_OUTPUT, "HTTP.output" );
    }
        
    /**
     * Set up any transport-specific derived properties in the message context.
     * @param context the context to set up
     * @param message the client service instance
     * @param engine the engine containing the registries
     * @param doLocal if true, we are setting up for local testing
     * @throws AxisFault if service cannot be found
     */
    public void setupMessageContext (MessageContext mc, ServiceClient serviceClient, Handler engine, boolean doLocal)
        throws AxisFault
    {
        if (url != null) mc.setProperty(URL, url);
        if (action != null) mc.setProperty(ACTION, action);
        try {
            mc.setTargetService( (String)mc.getProperty(ACTION) );
        } catch (AxisFault f) {
            System.err.println("HTTPClinet.setupMessageContext: Could not set target service to "+serviceClient.get(ACTION));
            throw f;
        }
        
        if ( doLocal && ((String)mc.getProperty(URL)).endsWith( ".jws") ) {
            mc.setProperty( "JWSFileName", serviceClient.get(URL).substring(11) );
            mc.setTargetService( Constants.JWSPROCESSOR_TARGET );
        }
    }
}
