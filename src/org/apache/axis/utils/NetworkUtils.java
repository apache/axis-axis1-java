/*
 * Copyright 2004 The Apache Software Foundation.
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

import org.apache.commons.logging.Log;
import org.apache.axis.components.logger.LogFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Utility classes for networking
 * created 13-May-2004 16:17:51
 */

public class NetworkUtils {
    /**
     * what we return when we cannot determine our hostname.
     * We use this rather than 'localhost' as if DNS is very confused,
     * localhost can map to different machines than "self".
     */
    public static final String LOCALHOST = "127.0.0.1";

    /**
     * keep this uninstantiable.
     */
    private NetworkUtils() {
    }

    protected static Log log =
            LogFactory.getLog(NetworkUtils.class.getName());

    /**
     * Get the string defining the hostname of the system, as taken from
     * the default network adapter of the system. There is no guarantee that
     * this will be fully qualified, or that it is the hostname used by external
     * machines to access the server.
     * If we cannot determine the name, then we return the default hostname,
     * which is defined by {@link #LOCALHOST}
     * @return a string name of the host.
     */
    public static String getLocalHostname() {
        InetAddress address;
        String hostname;
        try {
            address = InetAddress.getLocalHost();
            //force a best effort reverse DNS lookup
            hostname = address.getHostName();
            if (hostname == null || hostname.length() == 0) {
                hostname = address.toString();
            }
        } catch (UnknownHostException noIpAddrException) {

            //this machine is not on a LAN, or DNS is unhappy
            //return the default hostname
            if(log.isDebugEnabled()) {
                log.debug("Failed to lookup local IP address",noIpAddrException);
            }
            hostname = LOCALHOST;
        }
        return hostname;
    }
}
