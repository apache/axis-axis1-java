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
package org.apache.axis.ime;

/**
 * Interface that may be provided by MessageExchange impl's
 * to allow users to control the lifecycle of the "stuff"
 * going on under the covers
 * 
 * @author James M Snell (jasnell@us.ibm.com)
 */
public interface MessageExchangeLifecycle {

    /**
     * Initialize the lifecycle.  (Create threads, etc)
     */
    public void init();

    /**
     * Cleanup
     */
    public void cleanup()
           throws InterruptedException ;

    /**
     * Performs a "safe shutdown", allowing all
     * current activities to complete.
     */
    public void shutdown();

    /**
     * Performs an "unsafe shutdown", interrupting
     * all current activities without letting
     * them complete
     */
    public void shutdown(boolean force);

    /**
     * Block indefinitely until shutdown is 
     * complete.
     */
    public void awaitShutdown()
            throws InterruptedException;

    /**
     * Block for the specified amount of time 
     * or until shutdown is complete
     */
    public void awaitShutdown(long timeout)
            throws InterruptedException;

}
