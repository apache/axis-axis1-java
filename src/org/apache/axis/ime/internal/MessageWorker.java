/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

package org.apache.axis.ime.internal;

import org.apache.axis.ime.MessageChannel;
import org.apache.axis.ime.MessageExchangeContext;
import org.apache.axis.ime.MessageExchangeContextListener;

/**
 * @author James M Snell (jasnell@us.ibm.com)
 */
public class MessageWorker implements Runnable {

    protected static final long SELECT_TIMEOUT = 1000 * 30;

    protected MessageWorkerGroup pool;
    protected MessageChannel channel;
    protected MessageExchangeContextListener listener;

    private MessageWorker() {
    }

    public MessageWorker(
            MessageWorkerGroup pool,
            MessageChannel channel,
            MessageExchangeContextListener listener) {
        this.pool = pool;
        this.channel = channel;
        this.listener = listener;
    }

    public MessageExchangeContextListener getMessageExchangeContextListener() {
        return this.listener;
    }

    public MessageChannel getMessageChannel() {
        return this.channel;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            while (!pool.isShuttingDown()) {
                MessageExchangeContext context = channel.select(SELECT_TIMEOUT);
                if (context != null)
                    listener.onMessageExchangeContext(context);
            }
        } catch (Throwable t) {
            // kill the thread if any type of exception occurs.
            // don't worry, we'll create another one to replace it
            // if we're not currently in the process of shutting down.
            // once I get the logging function plugged in, we'll
            // log whatever errors do occur
        } finally {
            pool.workerDone(this);
        }
    }

}
