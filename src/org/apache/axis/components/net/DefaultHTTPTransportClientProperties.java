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
     * @see org.apache.axis.components.net.TransportClientProperties#getProxyUser()
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
     * @see org.apache.axis.components.net.TransportClientProperties#getProxyPassword()
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
