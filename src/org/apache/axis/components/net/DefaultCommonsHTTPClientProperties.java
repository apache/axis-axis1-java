/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
