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
public class DefaultHTTPSTransportClientProperties
    extends DefaultHTTPTransportClientProperties {

    /**
     * @see org.apache.axis.components.net.TransportClientProperties#getProxyHost()
     */
    public String getProxyHost() {
        if (proxyHost == null) {
            proxyHost = AxisProperties.getProperty("https.proxyHost");
            super.getProxyHost();
        }
        return proxyHost;
    }

    /**
     * @see org.apache.axis.components.net.TransportClientProperties#getNonProxyHosts()
     */
    public String getNonProxyHosts() {
        if (nonProxyHosts == null) {
            nonProxyHosts = AxisProperties.getProperty("https.nonProxyHosts");
            super.getNonProxyHosts();
        }
        return nonProxyHosts;
    }

    /**
     * @see org.apache.axis.components.net.TransportClientProperties#getPort()
     */
    public String getProxyPort() {
        if (proxyPort == null) {
            proxyPort = AxisProperties.getProperty("https.proxyPort");
            super.getProxyPort();
        }
        return proxyPort;
    }

    /**
     * @see org.apache.axis.components.net.TransportClientProperties#getUser()
     */
    public String getProxyUser() {
        if (proxyUser == null) {
            proxyUser = AxisProperties.getProperty("https.proxyUser");
            super.getProxyUser();
        }
        return proxyUser;
    }

    /**
     * @see org.apache.axis.components.net.TransportClientProperties#getPassword()
     */
    public String getProxyPassword() {
        if (proxyPassword == null) {
            proxyPassword = AxisProperties.getProperty("https.proxyPassword");
            super.getProxyPassword();
        }
        return proxyPassword;
    }
}
