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

package org.apache.axis.utils;

import org.apache.axis.components.net.TransportClientProperties;
import org.apache.axis.components.net.TransportClientPropertiesFactory;


/**
 * This class is used by WSDL2javaAntTask and WSDL2.
 * Supports the http.proxyUser and http.proxyPassword properties.
 */
public class DefaultAuthenticator extends java.net.Authenticator {
    private TransportClientProperties tcp = null;
    
    private String user;
    private String password;

    public DefaultAuthenticator(String user, String pass) {
        this.user = user;
        this.password = pass;
    }

    protected java.net.PasswordAuthentication getPasswordAuthentication() {
        // if user and password weren't provided, check the system properties
        if (user == null) {
            user = getTransportClientProperties().getProxyUser();
        }
        if (password == null) {
            password = getTransportClientProperties().getProxyPassword();
        }
        return new java.net.PasswordAuthentication(user, password.toCharArray());
    }
    
    private TransportClientProperties getTransportClientProperties() {
        if (tcp == null) {
            tcp = TransportClientPropertiesFactory.create("http");
        }
        return tcp;
    }
}
