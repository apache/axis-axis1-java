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

package org.apache.axis.client;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;

public class Transport {

    /**
     * Transport Chain Name - so users can change the default.
     */
    public String transportName = null ;

    /**
     * Transport URL, if any.
     */
    public String url = null;

    public final void setupMessageContext(MessageContext context,
                                          Call message,
                                          AxisEngine engine)
        throws AxisFault
    {
        if (url != null)
            context.setProperty(MessageContext.TRANS_URL, url);

        if (transportName != null)
            context.setTransportName(transportName);

        setupMessageContextImpl(context, message, engine);
    }

    public void setupMessageContextImpl(MessageContext context,
                                        Call message,
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


