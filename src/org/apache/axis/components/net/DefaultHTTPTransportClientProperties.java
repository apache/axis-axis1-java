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
package org.apache.axis.components.net;

import org.apache.axis.AxisProperties;


/**
 * @author Richard A. Sitze
 */
public class DefaultHTTPTransportClientProperties
    implements TransportClientProperties {
        
    private static final String emptyString = "";

    protected String proxyHost = null;
    protected String nonProxyHosts = null;
    protected String proxyPort = null;
    protected String proxyUser = null;
    protected String proxyPassword = null;


    /**
     * @see org.apache.axis.components.net.TransportClientProperties#getProxyHost()
     */
    public String getProxyHost() {
        if (proxyHost == null) {
            proxyHost = AxisProperties.getProperty("http.proxyHost");
            if (proxyHost == null)
                proxyHost = emptyString;
        }
        return proxyHost;
    }

    /**
     * @see org.apache.axis.components.net.TransportClientProperties#getNonProxyHosts()
     */
    public String getNonProxyHosts() {
        if (nonProxyHosts == null) {
            nonProxyHosts = AxisProperties.getProperty("http.nonProxyHosts");
            if (nonProxyHosts == null)
                nonProxyHosts = emptyString;
        }
        return nonProxyHosts;
    }

    /**
     * @see org.apache.axis.components.net.TransportClientProperties#getPort()
     */
    public String getProxyPort() {
        if (proxyPort == null) {
            proxyPort = AxisProperties.getProperty("http.proxyPort");
            if (proxyPort == null)
                proxyPort = emptyString;
        }
        return proxyPort;
    }

    /**
     * @see org.apache.axis.components.net.TransportClientProperties#getUser()
     */
    public String getProxyUser() {
        if (proxyUser == null) {
            proxyUser = AxisProperties.getProperty("http.proxyUser");
            if (proxyUser == null)
                proxyUser = emptyString;
        }
        return proxyUser;
    }

    /**
     * @see org.apache.axis.components.net.TransportClientProperties#getPassword()
     */
    public String getProxyPassword() {
        if (proxyPassword == null) {
            proxyPassword = AxisProperties.getProperty("http.proxyPassword");
            if (proxyPassword == null)
                proxyPassword = emptyString;
        }
        return proxyPassword;
    }
}
