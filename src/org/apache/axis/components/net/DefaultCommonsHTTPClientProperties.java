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
 * Default property set for the of the multi threaded connection pool
 * used in the CommonsHTTPSender transport implementation.  Values
 * returned by this implementation are identical to the defaults for
 * the Commons HTTPClient library itself, unless overridden with
 * Axis properties.
 *
 * @author Eric Friedman
 */
public class DefaultCommonsHTTPClientProperties implements CommonsHTTPClientProperties {

    /** the key for the Axis Property that controls the maximum total connections
        allowed in the httpclient pool */
    public static final String MAXIMUM_TOTAL_CONNECTIONS_PROPERTY_KEY =
        "axis.http.client.maximum.total.connections";

    /** the key for the Axis Property that controls the maximum connections
        per host allowed by the httpclient pool */
    public static final String MAXIMUM_CONNECTIONS_PER_HOST_PROPERTY_KEY =
        "axis.http.client.maximum.connections.per.host";

    /** the key for the Axis Property that sets the connection pool timeout
        for the httpclient pool */
    public static final String CONNECTION_POOL_TIMEOUT_KEY =
        "axis.http.client.connection.pool.timeout";

    /**
     * Convert the value for <tt>property</tt> into an int or, if none is found,
     * use the <tt>dephault</tt> value instead.
     *
     * @return an integer value
     */
    protected final int getIntegerProperty(String property, String dephault) {
        return Integer.parseInt(AxisProperties.getProperty(property, dephault));
    }

    /**
     * Return the integer value associated with the property 
     * axis.http.client.maximum.total.connections or a default of 20.
     *
     * @return a whole integer
     */
    public int getMaximumTotalConnections() {
        int i = getIntegerProperty(MAXIMUM_TOTAL_CONNECTIONS_PROPERTY_KEY, "20");
        if (i < 1) {
            throw new IllegalStateException(MAXIMUM_TOTAL_CONNECTIONS_PROPERTY_KEY + " must be > 1");
        }
        return i;
    }

    /**
     * Return the integer value associated with the property
     * axis.http.client.maximum.connections.per.host or a default of 2.
     *
     * @return a whole integer
     */
    public int getMaximumConnectionsPerHost() {
        int i = getIntegerProperty(MAXIMUM_CONNECTIONS_PER_HOST_PROPERTY_KEY, "2");
        if (i < 1) {
            throw new IllegalStateException(MAXIMUM_CONNECTIONS_PER_HOST_PROPERTY_KEY + " must be > 1");
        }
        return i;
    }

    /**
     * Return the integer value associated with the property
     * axis.http.client.connection.pool.timeout or a default of 0.
     *
     * @return an integer >= 0
     */
    public int getConnectionPoolTimeout() {
        int i = getIntegerProperty(CONNECTION_POOL_TIMEOUT_KEY, "0");
        if (i < 0) {
            throw new IllegalStateException(CONNECTION_POOL_TIMEOUT_KEY + " must be >= 0");
        }
        return i;
    }
}
