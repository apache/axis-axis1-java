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

/**
 * Interface implemented by classes seeking to configure the properties
 * of the multi threaded connection pool used in the CommonsHTTPSender
 * transport implementation.
 *
 * @author Eric Friedman
 */
public interface CommonsHTTPClientProperties {
    /**
     * Used to set the maximum number of connections that the pool can open
     * for all hosts.  Since connections imply sockets and sockets imply
     * file descriptors, the setting you use must not exceed any limits
     * your system imposes on the number of open file descriptors a
     * single process may have.
     *
     * @return an integer > 1
     */
    public int getMaximumTotalConnections();

    /**
     * Used to set the maximum number of connections that will be pooled
     * for a given host.  This setting is also constrained by 
     * the one returned from getMaximumTotalConnections.
     *
     * @return an integer > 1
     */
    public int getMaximumConnectionsPerHost();    

    /**
     * Used to set the amount of time, in milliseconds, spent waiting
     * for an available connection from the pool.  An exception is raised
     * if the timeout is triggered.
     *
     * @return an integer > 1 OR 0 for infinite timeout
     */
    public int getConnectionPoolTimeout();
}
