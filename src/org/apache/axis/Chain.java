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
 *        Apache Software Foundation (http://www.apache.org/)."
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
