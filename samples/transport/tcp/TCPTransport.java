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

package samples.transport.tcp;

import org.apache.axis.AxisEngine;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Transport;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import java.net.URL;

/**
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@allaire.com)
 */
public class TCPTransport extends Transport
{
    static Log log =
            LogFactory.getLog(TCPTransport.class.getName());

    private String host;
    private String port;
    
    public TCPTransport () {
        transportName = "tcp";
    }
    
    public TCPTransport (String host, String port) {
        transportName = "tcp";
        this.host = host;
        this.port = port;
    }
    
    /**
     * TCP properties
     */
    static public String HOST = "tcp.host";
    static public String PORT = "tcp.port";
    
    /**
     * Set up any transport-specific derived properties in the message context.
     * @param context the context to set up
     * @param message the client service instance
     * @param engine the engine containing the registries
     */
    public void setupMessageContextImpl(MessageContext mc,
                                        Call call,
                                        AxisEngine engine)
    {
        try {
          String urlString = mc.getStrProp(MessageContext.TRANS_URL);
          if (urlString != null) {
            URL url = new URL(urlString);
            host = url.getHost();
            port = new Integer(url.getPort()).toString();
          }
        } catch (java.net.MalformedURLException e) {
          // Do nothing here?
        }

        if (host != null) mc.setProperty(HOST, host);
        if (port != null) mc.setProperty(PORT, port);

        log.debug( "Port = " + mc.getStrProp(PORT));
        log.debug( "Host = " + mc.getStrProp(HOST));
        
        // kind of ugly... fake up a "http://host:port/" url to send down the chain
        // ROBJ TODO: clean this up so we use TCP transport properties all the way down
        // use serviceclient properties if any, otherwise use ours
        /*
        String url = "http://"+serv.get(HOST)+":"+serv.get(PORT);
        
        log.debug( "TCPTransport set URL to '" + url + "'");
        mc.setProperty(MessageContext.TRANS_URL, url);
        */
    }
}

