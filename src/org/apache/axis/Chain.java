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

package org.apache.axis ;



/**
 * A <code>Handler</code> that executes a 'chain' of child handlers in order.
 *
 * @author Doug Davis (dug@us.ibm.com.com)
 */

public interface Chain extends Handler {
    // fixme: if this can't be called after invoke, what exception should we
    //  document as being thrown if someone tries it?
    /**
     * Adds a handler to the end of the chain. May not be called after invoke.
     *
     * @param handler  the <code>Handler</code> to be added
     */
    public void addHandler(Handler handler);

    /**
     * Discover if a handler is in this chain.
     *
     * @param handler  the <code>Handler</code> to check
     * @return <code>true</code> if it is in this chain, <code>false</code>
     *              otherwise
     */
    public boolean contains(Handler handler);

    // fixme: do we want to use an array here, or a List? the addHandler method
    //  kind of indicates that the chain is dynamic
    // fixme: there's nothing in this contract about whether modifying this
    //  list of handlers will modify the chain or not - seems like a bad idea to
    //  expose the stoorage as we have addHandler and contains methods.
    // fixme: would adding an iterator, size and remove method mean we could
    //  drop this entirely?
    /**
     * Get the list of handlers in the chain. Is Handler[] the right form?
     *
     * @return an array of <code>Handler</code>s that have been added
     */
    public Handler[] getHandlers();

    // How many do we want to force people to implement?
};
