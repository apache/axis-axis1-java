/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axis.transport.http;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Transport;

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
    public static final String DEFAULT_TRANSPORT_NAME = "http";
    
    /**
     * HTTP properties
     */
    public static final String URL = MessageContext.TRANS_URL;

    private String cookie;
    private String cookie2;
    private String action;
    
    public HTTPTransport () {
        transportName = DEFAULT_TRANSPORT_NAME;
    }
    
    /**
     * helper constructor
     */
    public HTTPTransport (String url, String action)
    {
        transportName = DEFAULT_TRANSPORT_NAME;
        this.url = url;
        this.action = action;
    }
    
    /**
     * Set up any transport-specific derived properties in the message context.
     * @param mc the context to set up
     * @param call the call (unused?)
     * @param engine the engine containing the registries
     * @throws AxisFault if service cannot be found
     */
    public void setupMessageContextImpl(MessageContext mc,
                                        Call call,
                                        AxisEngine engine)
        throws AxisFault
    {
        if (action != null) {
            mc.setUseSOAPAction(true);
            mc.setSOAPActionURI(action);
        }

        // Set up any cookies we know about
        if (cookie != null)
            mc.setProperty(HTTPConstants.HEADER_COOKIE, cookie);
        if (cookie2 != null)
            mc.setProperty(HTTPConstants.HEADER_COOKIE2, cookie2);

        // Allow the SOAPAction to determine the service, if the service
        // (a) has not already been determined, and (b) if a service matching
        // the soap action has been deployed.
        if (mc.getService() == null) {
            mc.setTargetService( (String)mc.getSOAPActionURI() );
        }
    }

    public void processReturnedMessageContext(MessageContext context) {
        cookie = context.getStrProp(HTTPConstants.HEADER_COOKIE);
        cookie2 = context.getStrProp(HTTPConstants.HEADER_COOKIE2);
    }
}
