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

package samples.transport.tcp;

import java.net.URL;
import java.util.* ;
import org.apache.axis.* ;
import org.apache.axis.utils.Debug ;
import org.apache.axis.handlers.* ;
import org.apache.axis.registries.* ;
import org.apache.axis.client.Transport;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.ServiceClient;

/**
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@allaire.com)
 */
public class TCPTransport extends Transport
{
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
                                        ServiceClient serv,
                                        AxisEngine engine)
    {
        try {
          String urlString = mc.getStrProp(MessageContext.TRANS_URL);
          if (urlString != null) {
            URL url = new URL(urlString);
            mc.setProperty(HOST, url.getHost());
            mc.setProperty(PORT, new Integer(url.getPort()).toString());
          }
        } catch (java.net.MalformedURLException e) {
          // Do nothing here?
        }

        if (host != null) serv.set(HOST, host);
        if (port != null) serv.set(PORT, port);

        Debug.Print(3, "Port = " + mc.getStrProp(PORT));
        Debug.Print(3, "Host = " + mc.getStrProp(HOST));
        
        // kind of ugly... fake up a "http://host:port/" url to send down the chain
        // ROBJ TODO: clean this up so we use TCP transport properties all the way down
        // use serviceclient properties if any, otherwise use ours
        /*
        String url = "http://"+serv.get(HOST)+":"+serv.get(PORT);
        
        Debug.Print(4, "TCPTransport set URL to '" + url + "'");
        mc.setProperty(MessageContext.TRANS_URL, url);
        */
    }
}

